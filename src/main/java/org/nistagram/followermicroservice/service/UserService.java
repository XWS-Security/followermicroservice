package org.nistagram.followermicroservice.service;

import org.nistagram.followermicroservice.data.model.User;

public interface UserService {
    void saveUser(User user);

    void updateUser(User user);
}
