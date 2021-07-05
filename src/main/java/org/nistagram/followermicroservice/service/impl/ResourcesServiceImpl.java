package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.controller.dto.FollowingStatusDto;
import org.nistagram.followermicroservice.controller.dto.InteractionDto;
import org.nistagram.followermicroservice.controller.dto.UserDto;
import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.Interaction;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.nistagram.followermicroservice.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourcesServiceImpl implements ResourcesService {
    private final InteractionRepository interactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResourcesServiceImpl(InteractionRepository interactionRepository, UserRepository userRepository) {
        this.interactionRepository = interactionRepository;
        this.userRepository = userRepository;
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
                return new FollowingStatusDto("NOT_FOLLOWING", null, false, false);
            }
            if (reverse.getFollowingStatus() == FollowingStatus.FOLLOWING) {
                return new FollowingStatusDto("FOLLOWS_YOU", null, false, false);
            }
            return new FollowingStatusDto("NOT_FOLLOWING", null, false, false);
        }

        if (interaction.getFollowingStatus() == FollowingStatus.FOLLOWING) {
            String notifications = interaction.isNotificationsOn() ? "ON" : "OFF";
            boolean muted = interaction.isMuted();
            return new FollowingStatusDto("FOLLOWING", notifications, muted, false);
        }

        if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
            return new FollowingStatusDto("REQUEST_SENT", null, false, false);
        }

        return new FollowingStatusDto("BLOCKED", null, false, true);
    }

    @Override
    public String canHire(String username) {
        var status = getFollowingStatus(username);
        var user = userRepository.findByUsername(username);
        if (!user.isProfilePrivate()) {
            if (status.getFollowing().equals("REQUEST_SENT")) return "REQUEST_SENT";
            else if (status.getFollowing().equals("FOLLOWING")) return "FOLLOWING";
            else return "NOT_FOLLOWING";
        }
        return "PUBLIC";
    }

    @Override
    public int getNumOfFollowers(String username) {
        return interactionRepository.getNumberOfFollowers(username);
    }

    @Override
    public int getNumOfFollowing(String username) {
        return interactionRepository.getNumberOfFollowing(username);
    }

    @Override
    public List<UserDto> getFollowers() {
        User user = getCurrentlyLoggedUser();
        return interactionRepository.findFollowers(user.getUsername()).stream()
                .map(u -> new UserDto(u.getUsername(), u.isProfilePrivate())).collect(Collectors.toList());
    }

    private User getCurrentlyLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
