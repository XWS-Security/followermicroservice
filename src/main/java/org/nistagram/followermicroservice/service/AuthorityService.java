package org.nistagram.followermicroservice.service;

import org.nistagram.followermicroservice.data.model.Role;

import java.util.List;

public interface AuthorityService {
    List<Role> findById(Long id);

    List<Role> findByname(String name);
}
