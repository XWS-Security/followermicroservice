package org.nistagram.followermicroservice.data.repository;

import org.nistagram.followermicroservice.data.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(String username);
}
