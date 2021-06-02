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

import java.util.Map;

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
        if (!editUserDto.getOldUsername().equals(editUserDto.getUsername()) && !isUsernameAvailable(editUserDto.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        if (loadedUser.isProfilePrivate() && !editUserDto.isProfilePrivate()) {
            updatePendingFollowRequests(loadedUser);
        }
        userRepository.updateProperties(loadedUser.getId(), editUserDto.getUsername(), editUserDto.isProfilePrivate());
    }

    // The query number is not optimal here but there have been issues with saveAll function
    // Also this method is an edge case and is not going to be used very often
    private void updatePendingFollowRequests(User user) {
        String followee = user.getUsername();
        for (Map.Entry<User, Interaction> pair : user.getFollowers().entrySet()) {
            if (pair.getValue().getFollowingStatus() == FollowingStatus.WAITING_FOR_APPROVAL) {
                String follower = pair.getKey().getUsername();
                interactionRepository.updateFollowingStatus(follower, followee, FollowingStatus.FOLLOWING.toString());
            }
        }
    }

    private boolean isUsernameAvailable(String username) {
        User user = userRepository.findByUsername(username);
        return user == null;
    }
}
