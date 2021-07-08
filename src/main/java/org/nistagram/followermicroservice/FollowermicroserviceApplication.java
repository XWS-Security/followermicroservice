package org.nistagram.followermicroservice;

import org.nistagram.followermicroservice.data.model.Role;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.data.repository.InteractionRepository;
import org.nistagram.followermicroservice.data.repository.RoleRepository;
import org.nistagram.followermicroservice.data.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FollowermicroserviceApplication {

    public static void main(String[] args) {
//        SpringApplication.run(FollowermicroserviceApplication.class, args);
        ApplicationContext ctx = SpringApplication.run(FollowermicroserviceApplication.class, args);

        // insert roles into database
        RoleRepository roleRepository = (RoleRepository) ctx.getBean("roleRepository");
        Role role = new Role("NISTAGRAM_USER_ROLE");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        roleRepository.save(role);

        // insert test data
        User luka = new User("luka", true);
        User vlado = new User("vlado", false);
        User vidoje = new User("vidoje", true);
        User milica = new User("milica", true);
        User duja = new User("duja", false);
        User kobra = new User("kobra", true);

        luka.setRoles(roles);
        vlado.setRoles(roles);
        vidoje.setRoles(roles);
        milica.setRoles(roles);
        duja.setRoles(roles);
        kobra.setRoles(roles);

        List<User> users = new ArrayList<>();
        users.add(luka);
        users.add(vlado);
        users.add(vidoje);
        users.add(milica);
        users.add(duja);
        users.add(kobra);

        UserRepository userRepository = (UserRepository) ctx.getBean("userRepository");
        userRepository.saveAll(users);

        InteractionRepository interactionRepository = (InteractionRepository) ctx.getBean("interactionRepository");
        interactionRepository.saveRelationship("luka", "vlado", "FOLLOWING");
        interactionRepository.saveRelationship("vlado", "luka", "FOLLOWING");
        interactionRepository.saveRelationship("vlado", "vidoje", "FOLLOWING");
        interactionRepository.saveRelationship("milica", "duja", "FOLLOWING");
        interactionRepository.saveRelationship("duja", "milica", "FOLLOWING");
        interactionRepository.saveRelationship("milica", "luka", "FOLLOWING");
        interactionRepository.saveRelationship("vlado", "kobra", "FOLLOWING");
        interactionRepository.saveRelationship("luka", "duja", "BLOCKED");
    }
}
