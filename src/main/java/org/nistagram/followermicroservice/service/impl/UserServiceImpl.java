package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.Interaction;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.nistagram.followermicroservice.exception.UserDoesNotExistException;
import org.nistagram.followermicroservice.exception.UsernameAlreadyExistsException;
import org.nistagram.followermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, InteractionRepository interactionRepository) {
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public void saveUser(User user) throws UsernameAlreadyExistsException {
        if (isUsernameAvailable(user.getUsername())) {
            userRepository.save(user);
        } else {
            throw new UsernameAlreadyExistsException();
        }
    }

    @Override
    public void updateUser(EditUserDto editUserDto) {
        User loadedUser = userRepository.findByUsername(editUserDto.getOldUsername());
        if (loadedUser == null) {
            throw new UserDoesNotExistException();
        }
        if (!isUsernameAvailable(editUserDto.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        // TODO: update pending follow requests
//        if (loadedUser.isProfilePrivate() && !editUserDto.isProfilePrivate()) {
//            updatePendingFollowRequests(loadedUser);
//        }
        userRepository.updateProperties(loadedUser.getId(), editUserDto.getUsername(), editUserDto.isProfilePrivate());
    }

    // TODO: Fix this (does not update relationships)
    private void updatePendingFollowRequests(User user) {
        List<Interaction> modified = new ArrayList<>();
        for (Interaction interaction : user.getFollowers().values()) {
            if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
                interaction.acceptFollowingRequest();
                modified.add(interaction);
            }
        }
        interactionRepository.saveAll(modified);
    }

    private boolean isUsernameAvailable(String username) {
        User user = userRepository.findByUsername(username);
        return user == null;
    }
}
