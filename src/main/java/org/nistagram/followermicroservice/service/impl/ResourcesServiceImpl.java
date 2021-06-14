package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.controller.dto.FollowingStatusDto;
import org.nistagram.followermicroservice.controller.dto.InteractionDto;
import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.Interaction;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourcesServiceImpl implements ResourcesService {
    private final InteractionRepository interactionRepository;

    @Autowired
    public ResourcesServiceImpl(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @Override
    public List<InteractionDto> getWaitingForApproval() {
        List<InteractionDto> result = new ArrayList<>();
        for (String follower : interactionRepository.findAllWaiting(getCurrentlyLoggedUser().getUsername())) {
            result.add(new InteractionDto(follower, FollowingStatus.WAITING_FOR_APPROVAL.toString()));
        }
        return result;
    }

    @Override
    public FollowingStatusDto getFollowingStatus(String username) {
        User user = getCurrentlyLoggedUser();
        Interaction interaction = interactionRepository.findRelationship(user.getUsername(), username);
        Interaction reverse = interactionRepository.findRelationship(username, user.getUsername());

        if (interaction == null) {
            if (reverse == null) {
                return new FollowingStatusDto("NOT_FOLLOWING", null, false);
            }
            if (reverse.getFollowingStatus() == FollowingStatus.FOLLOWING) {
                return new FollowingStatusDto("FOLLOWS_YOU", null, false);
            }
            return new FollowingStatusDto("NOT_FOLLOWING", null, false);
        }

        if (interaction.getFollowingStatus() == FollowingStatus.FOLLOWING) {
            String notifications = interaction.isNotificationsOn() ? "ON" : "OFF";
            return new FollowingStatusDto("FOLLOWING", notifications, false);
        }

        if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
            return new FollowingStatusDto("REQUEST_SENT", null, false);
        }

        return new FollowingStatusDto("BLOCKED", null, true);
    }

    @Override
    public int getNumOfFollowers(String username) {
        return interactionRepository.getNumberOfFollowers(username);
    }

    @Override
    public int getNumOfFollowing(String username) {
        return interactionRepository.getNumberOfFollowing(username);
    }

    private User getCurrentlyLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
