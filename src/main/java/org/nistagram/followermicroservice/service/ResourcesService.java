package org.nistagram.followermicroservice.service;

import org.nistagram.followermicroservice.controller.dto.InteractionDto;

import java.util.List;

public interface ResourcesService {
    List<InteractionDto> getWaitingForApproval();
    String getFollowingStatus(String username);
}
