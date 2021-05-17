package org.nistagram.followermicroservice.service.impl;

import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.UserRepository;
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
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
