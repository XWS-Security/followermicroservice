package org.nistagram.followermicroservice.service.impl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.nistagram.followermicroservice.controller.dto.SubscriptionDto;
import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.Interaction;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.nistagram.followermicroservice.exception.*;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
public class FollowServiceImpl implements FollowService {
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Value("${CONTENT}")
    private String contentMicroserviceURI;

    @Autowired
    public FollowServiceImpl(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void follow(String followeeUsername, String token) throws SSLException {
        User follower = getCurrentlyLoggedUser();
        User followee = userRepository.findByUsername(followeeUsername);
        validateFollowRequest(follower, followee, follower.getUsername(), followeeUsername);

        FollowingStatus followingStatus;
        if (followee.isProfilePrivate()) {
            followingStatus = FollowingStatus.WAITING_FOR_APPROVAL;
        } else {
            followingStatus = FollowingStatus.FOLLOWING;
            SubscriptionDto dto = new SubscriptionDto(followeeUsername, follower.getUsername());
            updateSubscription(dto, token, true);
        }

        if (!followee.isFollowing(follower) && !followee.isWaitingForApproval(follower)) {
            interactionRepository.saveRelationship(follower.getUsername(), followeeUsername, followingStatus.toString());
            loggerService.logFollowRequestSuccess(follower.getUsername(), followeeUsername);
        }
    }

    @Override
    public void unfollow(String followeeUsername, String token) throws SSLException {
        User follower = getCurrentlyLoggedUser();
        User followee = userRepository.findByUsername(followeeUsername);
        validateFollowRequest(follower, followee, follower.getUsername(), followeeUsername);

        Interaction interaction = interactionRepository.findRelationship(follower.getUsername(), followeeUsername);
        validateFollowRequestForUnfollow(interaction);

        interactionRepository.deleteRelationship(follower.getUsername(), followeeUsername);
        SubscriptionDto dto = new SubscriptionDto(followeeUsername, follower.getUsername());
        updateSubscription(dto, token, false);
        loggerService.logUnfollowRequestSuccess(follower.getUsername(), followeeUsername);
    }

    @Override
    public void acceptFollowRequest(String followerUsername, String token) throws SSLException {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = getCurrentlyLoggedUser();
        validateFollowRequest(follower, followee, followerUsername, followee.getUsername());

        Interaction interaction = interactionRepository.findRelationship(followerUsername, followee.getUsername());
        validateFollowRequestForApproval(interaction);

        interactionRepository.updateFollowingStatus(followerUsername, followee.getUsername(), FollowingStatus.FOLLOWING.toString());
        SubscriptionDto dto = new SubscriptionDto(followee.getUsername(), followerUsername);
        updateSubscription(dto, token, true);
        loggerService.logFollowRequestApprovalSuccess(followerUsername, followee.getUsername());
    }

    @Override
    public void rejectFollowRequest(String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = getCurrentlyLoggedUser();
        validateFollowRequest(follower, followee, followerUsername, followee.getUsername());

        Interaction interaction = interactionRepository.findRelationship(followerUsername, followee.getUsername());
        validateFollowRequestForApproval(interaction);

        interactionRepository.deleteRelationship(followerUsername, followee.getUsername());
        loggerService.logFollowRequestRejectionSuccess(followerUsername, followee.getUsername());
    }

    @Override
    public void validateInteractionAccess(String username) {
        String currentUsername = getCurrentlyLoggedUser().getUsername();
        Interaction interaction = interactionRepository.findRelationship(currentUsername, username);

        if (interaction == null) {
            throw new FollowRequestDoesNotExistException();
        }

        if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new FollowRequestNotApprovedException();
        }

        if (interaction.getFollowingStatus() == FollowingStatus.BLOCKED) {
            throw new InvalidFollowRequestUserIsBlockedException();
        }
    }

    @Override
    public void validateViewAccess(String username) {
        User user = userRepository.findByUsername(username);
        if (!user.isProfilePrivate()) return;

        User currentUser = getCurrentlyLoggedUser();
        if (currentUser == null) throw new AnonymousUserCannotAccessPrivateProfileContentException();

        if (username.equals(currentUser.getUsername())) return;

        Interaction interaction = interactionRepository.findRelationship(currentUser.getUsername(), username);
        if (interaction == null) throw new FollowRequestDoesNotExistException();

        if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new FollowRequestNotApprovedException();
        }

        if (interaction.getFollowingStatus() == FollowingStatus.BLOCKED) {
            throw new InvalidFollowRequestUserIsBlockedException();
        }
    }

    private User getCurrentlyLoggedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            return null;
        } else {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    private void validateFollowRequest(User follower, User followee, String followerUsername, String followeeUsername) {
        if (follower == null) {
            throw new UserDoesNotExistException(followerUsername);
        }
        if (followee == null) {
            throw new UserDoesNotExistException(followeeUsername);
        }
        if (followee.isBlocked(follower)) {
            throw new FollowRequestFailedBlockedUserException();
        }
        if (follower.isBlocked(followee)) {
            throw new InvalidFollowRequestUserIsBlockedException();
        }
    }

    private void validateFollowRequestForApproval(Interaction followRequest) {
        if (followRequest == null) {
            throw new FollowRequestDoesNotExistException();
        }
        if (followRequest.getFollowingStatus() != FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new FollowRequestIsAlreadyAcceptedException();
        }
    }

    private void validateFollowRequestForUnfollow(Interaction followRequest) {
        if (followRequest == null) {
            throw new FollowRequestDoesNotExistException();
        }
        if (followRequest.getFollowingStatus() != FollowingStatus.FOLLOWING && followRequest.getFollowingStatus() != FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new UserIsNotFollowedException();
        }
    }

    private void updateSubscription(SubscriptionDto subscriptionDto, String token, boolean subscribe) throws SSLException {
        String path = (subscribe) ? "subscribe/" : "subscribe/unsubscribe";

        // SSL
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

        // Creating web client.
        WebClient client = WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // Define a method.
        var result = client.put()
                .uri(path)
                .headers(h -> h.setBearerAuth(token))
                .body(Mono.just(subscriptionDto), SubscriptionDto.class)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe();
    }
}
