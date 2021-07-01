package org.nistagram.followermicroservice.service;

import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.data.model.User;

public interface UserService {
    void saveUser(User user);

    void updateUser(EditUserDto editUserDto);

    void deleteUser(User user);
}
