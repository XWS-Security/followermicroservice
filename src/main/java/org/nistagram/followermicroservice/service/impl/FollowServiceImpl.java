package org.nistagram.followermicroservice.service.impl;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public FollowServiceImpl(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void follow(String followeeUsername) {
        User follower = getCurrentlyLoggedUser();
        User followee = userRepository.findByUsername(followeeUsername);
        validateFollowRequest(follower, followee, follower.getUsername(), followeeUsername);

        FollowingStatus followingStatus;
        if (followee.isProfilePrivate()) {
            followingStatus = FollowingStatus.WAITING_FOR_APPROVAL;
        } else {
            followingStatus = FollowingStatus.FOLLOWING;
        }

        if (!followee.isFollowing(follower) && !followee.isWaitingForApproval(follower)) {
            interactionRepository.saveRelationship(follower.getUsername(), followeeUsername, followingStatus.toString());
            loggerService.logFollowRequestSuccess(follower.getUsername(), followeeUsername);
        }
    }

    @Override
    public void unfollow(String followeeUsername) {
        User follower = getCurrentlyLoggedUser();
        User followee = userRepository.findByUsername(followeeUsername);
        validateFollowRequest(follower, followee, follower.getUsername(), followeeUsername);

        Interaction interaction = interactionRepository.findRelationship(follower.getUsername(), followeeUsername);
        validateFollowRequestForUnfollow(interaction);

        interactionRepository.deleteRelationship(follower.getUsername(), followeeUsername);
        loggerService.logUnfollowRequestSuccess(follower.getUsername(), followeeUsername);
    }

    @Override
    public void acceptFollowRequest(String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = getCurrentlyLoggedUser();
        validateFollowRequest(follower, followee, followerUsername, followee.getUsername());

        Interaction interaction = interactionRepository.findRelationship(followerUsername, followee.getUsername());
        validateFollowRequestForApproval(interaction);

        interactionRepository.updateFollowingStatus(followerUsername, followee.getUsername(), FollowingStatus.FOLLOWING.toString());
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
    public void validateAccess(String username) {
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
}
