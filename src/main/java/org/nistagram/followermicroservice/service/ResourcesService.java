package org.nistagram.followermicroservice.service;

import org.nistagram.followermicroservice.controller.dto.FollowingStatusDto;
import org.nistagram.followermicroservice.controller.dto.InteractionDto;
import org.nistagram.followermicroservice.controller.dto.UserDto;

import java.util.List;

public interface ResourcesService {
    List<InteractionDto> getWaitingForApproval();

    FollowingStatusDto getFollowingStatus(String username);

    FollowingStatusDto getReverseFollowingStatus(String username);

    int getNumOfFollowers(String username);

    int getNumOfFollowing(String username);

    String canHire(String username);

    List<UserDto> getFollowers();
}
