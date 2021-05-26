package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.nistagram.followermicroservice.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;

    @Autowired
    public FollowServiceImpl(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void follow(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        FollowingStatus followingStatus;
        if (followee.isProfilePrivate()) {
            followingStatus = FollowingStatus.WAITING_FOR_APPROVAL;
        } else {
            followingStatus = FollowingStatus.FOLLOWING;
        }

        // TODO: check if relationship already exists
        interactionRepository.saveRelationship(followerUsername, followeeUsername, followingStatus.toString());
    }

    @Override
    public void unfollow(String followerUsername, String followeeUsername) {

    }

    @Override
    public void approveFollowRequest(String followerUsername, String followeeUsername) {

    }

    @Override
    public void rejectFollowRequest(String followerUsername, String followeeUsername) {

    }
}
