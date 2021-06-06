package org.nistagram.followermicroservice.service.impl;

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
    public String getFollowingStatus(String username) {
        User user = getCurrentlyLoggedUser();
        Interaction interaction = interactionRepository.findRelationship(user.getUsername(), username);
        if (interaction == null) {
            return "NOT_FOLLOWING";
        }
        return interaction.getFollowingStatus().toString();
    }

    private User getCurrentlyLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
