package org.nistagram.followermicroservice;

import org.nistagram.followermicroservice.data.model.Role;
import org.nistagram.followermicroservice.data.repository.RoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FollowermicroserviceApplication {

    public static void main(String[] args) {
//        SpringApplication.run(FollowermicroserviceApplication.class, args);
        // TODO: Comment out if data already exists
        ApplicationContext ctx = SpringApplication.run(FollowermicroserviceApplication.class, args);

        // insert roles into database
        RoleRepository roleRepository = (RoleRepository) ctx.getBean("roleRepository");
        Role role = new Role("NISTAGRAM_USER_ROLE");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
//        roleRepository.save(role);
    }
}
