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
import org.nistagram.followermicroservice.exception.CannotUnblockUserException;
import org.nistagram.followermicroservice.exception.FollowRequestFailedBlockedUserException;
import org.nistagram.followermicroservice.exception.InvalidFollowRequestUserIsBlockedException;
import org.nistagram.followermicroservice.exception.UserDoesNotExistException;
import org.nistagram.followermicroservice.service.BlockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
public class BlockServiceImpl implements BlockService {
    @Value("${CONTENT}")
    private String contentMicroserviceURI;

    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;

    public BlockServiceImpl(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void block(String username, String token) throws SSLException {
        String currentUsername = getCurrentlyLoggedUser().getUsername();
        validateUsers(currentUsername, username);

        Interaction interaction = interactionRepository.findRelationship(currentUsername, username);
        if (interaction == null) {
            interactionRepository.saveRelationship(currentUsername, username, FollowingStatus.BLOCKED.toString());
        } else {
            interactionRepository.updateFollowingStatus(currentUsername, username, FollowingStatus.BLOCKED.toString());
        }

        Interaction reverse = interactionRepository.findRelationship(username, currentUsername);
        if (reverse != null) {
            interactionRepository.deleteRelationship(username, currentUsername);
        }

        SubscriptionDto subscriptionDto = new SubscriptionDto(currentUsername, username);
        updateSubscription(subscriptionDto, token, false);
    }

    @Override
    public void unblock(String username) {
        String currentUsername = getCurrentlyLoggedUser().getUsername();
        validateUsers(currentUsername, username);

        Interaction interaction = interactionRepository.findRelationship(currentUsername, username);
        if (interaction == null) {
            throw new CannotUnblockUserException();
        } else {
            interactionRepository.deleteRelationship(currentUsername, username);
        }
    }

    private void validateUsers(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        if (follower == null) {
            throw new UserDoesNotExistException(followerUsername);
        }
        if (followee == null) {
            throw new UserDoesNotExistException(followeeUsername);
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

    private User getCurrentlyLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
