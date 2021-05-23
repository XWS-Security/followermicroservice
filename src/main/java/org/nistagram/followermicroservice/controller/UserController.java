package org.nistagram.followermicroservice.controller;

import org.modelmapper.ModelMapper;
import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.controller.dto.UserDto;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.exception.UsernameAlreadyExistsException;
import org.nistagram.followermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            userService.saveUser(modelMapper.map(userDto, User.class));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
    public ResponseEntity<String> updateUser(@RequestBody @Valid EditUserDto editUserDto) {
        try {
            userService.updateUser(editUserDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("hit") // Purpose of this method is to show communication between microservices
    public ResponseEntity<String> hitMeBabyOneMoreTime() {
        return new ResponseEntity<>("Uspio sam zemo!", HttpStatus.OK);
    }
}
