package org.nistagram.followermicroservice.controller;

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
        User user = new User() {{
            setUsername("milica");
            setProfilePrivate(true);
            setFollowers(new HashMap<>());
            setFollowing(new HashMap<>());
        }};
        userService.saveUser(user);
        return new ResponseEntity<>("User saved", HttpStatus.OK);
    }
}
