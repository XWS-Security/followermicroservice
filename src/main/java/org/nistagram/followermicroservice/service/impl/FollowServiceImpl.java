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
    public void follow(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        validateFollowRequest(follower, followee);

        FollowingStatus followingStatus;
        if (followee.isProfilePrivate()) {
            followingStatus = FollowingStatus.WAITING_FOR_APPROVAL;
        } else {
            followingStatus = FollowingStatus.FOLLOWING;
        }

        if (!followee.isFollowing(follower) && !followee.isWaitingForApproval(follower)) {
            interactionRepository.saveRelationship(followerUsername, followeeUsername, followingStatus.toString());
            loggerService.logFollowRequestSuccess(followerUsername, followeeUsername);
        }
    }

    @Override
    public void unfollow(String followerUsername, String followeeUsername) {
        // TODO:
    }

    @Override
    public void acceptFollowRequest(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        validateFollowRequest(follower, followee);

        Interaction interaction = interactionRepository.getRelationship(followerUsername, followeeUsername);
        if (interaction == null) {
            throw new FollowRequestDoesNotExistException();
        }

        if (interaction.getFollowingStatus() != FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new FollowRequestIsAlreadyAcceptedException();
        }

        interactionRepository.updateFollowingStatus(followerUsername, followeeUsername, FollowingStatus.FOLLOWING.toString());
    }

    @Override
    public void rejectFollowRequest(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        validateFollowRequest(follower, followee);

        Interaction interaction = interactionRepository.getRelationship(followerUsername, followeeUsername);
        if (interaction == null) {
            throw new FollowRequestDoesNotExistException();
        }

        if (interaction.getFollowingStatus() != FollowingStatus.WAITING_FOR_APPROVAL) {
            throw new FollowRequestIsAlreadyAcceptedException();
        }

        // TODO: Delete interaction
    }

    private void validateFollowRequest(User follower, User followee) {
        if (follower == null) {
            throw new UserDoesNotExistException();
        }
        if (followee == null) {
            throw new UserDoesNotExistException();
        }
        if (followee.isBlocked(follower)) {
            throw new FollowRequestFailedBlockedUserException();
        }
        if (follower.isBlocked(followee)) {
            throw new InvalidFollowRequestUserIsBlockedException();
        }
    }
}
