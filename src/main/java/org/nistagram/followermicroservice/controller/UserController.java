package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.data.model.FollowingStatus;
import org.nistagram.followermicroservice.data.model.Interaction;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<String> createAuthenticationToken() {
        User user = new User();
        user.setUsername("lule");
        user.setProfilePrivate(false);

        User user2 = new User();
        user2.setUsername("dovla");
        user2.setProfilePrivate(true);

        Interaction interaction = new Interaction();
        interaction.setFollowingStatus(FollowingStatus.FOLLOWING);
        interaction.setMuted(false);
        interaction.setNotificationsOn(true);

        HashMap<User, Interaction> followers = new HashMap<>();
        followers.put(user2, interaction);
        user.setFollowers(followers);

        Interaction interaction2 = new Interaction();
        interaction2.setFollowingStatus(FollowingStatus.WAITING_FOR_APPROVAL);
        interaction2.setMuted(false);
        interaction2.setNotificationsOn(false);

        HashMap<User, Interaction> followers2 = new HashMap<>();
        followers2.put(user, interaction2);
        user2.setFollowers(followers2);

        userService.saveUser(user);
        userService.saveUser(user2);

        return new ResponseEntity<>("User saved", HttpStatus.OK);
    }
}
