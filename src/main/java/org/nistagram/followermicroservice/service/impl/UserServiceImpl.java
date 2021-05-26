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
        User loadedUser = userRepository.findByUsername(user.getUsername());
        if (loadedUser != null) {
            throw new UsernameAlreadyExistsException();
        }
        userRepository.save(user);
    }

    @Override
    public void updateUser(EditUserDto editUserDto) {
        User loadedUser = userRepository.findByUsername(editUserDto.getOldUsername());
        if (loadedUser == null) {
            throw new UserDoesNotExistException();
        }
        if (loadedUser.isProfilePrivate() && !editUserDto.isProfilePrivate()) {
            updatePendingFollowRequests(loadedUser);
        }
        userRepository.updateProperties(loadedUser.getId(), editUserDto.getUsername(), editUserDto.isProfilePrivate());
    }

    private void updatePendingFollowRequests(User user) {
        System.out.println("Updating follow requests...");
        List<Interaction> modified = new ArrayList<>();
        user.getFollowers().forEach((user1, interaction) -> {
            if (interaction.getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
                interaction.acceptFollowingRequest();
                modified.add(interaction);
                System.out.println("Follow request " + interaction.getId() + " should be modified.");
            }
        });
        System.out.println("Interactions to modify: " + modified.size());
        interactionRepository.saveAll(modified);
        System.out.println("Interactions saved.");
    }
}
