package org.nistagram.followermicroservice.controller;

import org.modelmapper.ModelMapper;
import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.controller.dto.UserDto;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.exception.UserDoesNotExistException;
import org.nistagram.followermicroservice.exception.UsernameAlreadyExistsException;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            loggerService.logCreateUser(userDto.getUsername());
            userService.saveUser(modelMapper.map(userDto, User.class));
            loggerService.logCreateUserSuccess(userDto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            loggerService.logCreateUserFail(userDto.getUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> updateUser(@RequestBody @Valid EditUserDto editUserDto) {
        try {
            loggerService.logUpdateUser(editUserDto.getUsername(), editUserDto.getOldUsername());
            userService.updateUser(editUserDto);
            loggerService.logUpdateUserSuccess(editUserDto.getUsername(), editUserDto.getOldUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameAlreadyExistsException | UserDoesNotExistException e) {
            loggerService.logUpdateUserFail(editUserDto.getUsername(), editUserDto.getOldUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @GetMapping("hit") // Purpose of this method is to show communication between microservices
//    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> hitMeBabyOneMoreTime() {
        return new ResponseEntity<>("Uspio sam zemo!", HttpStatus.OK);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        loggerService.logValidationFailed(e.getMessage());
        return new ResponseEntity<>("Invalid characters in request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        loggerService.logValidationFailed(e.getMessage());
        return new ResponseEntity<>("Invalid characters in request", HttpStatus.BAD_REQUEST);
    }
}
