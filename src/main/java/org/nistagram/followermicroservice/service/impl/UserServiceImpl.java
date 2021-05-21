package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.nistagram.followermicroservice.exception.UserDoesNotExistException;
import org.nistagram.followermicroservice.exception.UsernameAlreadyExistsException;
import org.nistagram.followermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void updateUser(User user) {
        User loadedUser = userRepository.findByUsername(user.getUsername());
        if (loadedUser == null) {
            throw new UserDoesNotExistException();
        }
        loadedUser.setProfilePrivate(user.isProfilePrivate());
        // TODO: if user is switched to public update all follower requests to accepted
        userRepository.save(loadedUser);
    }
}
