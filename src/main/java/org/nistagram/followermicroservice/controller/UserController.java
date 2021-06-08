package org.nistagram.followermicroservice.controller;

import org.modelmapper.ModelMapper;
import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.controller.dto.HasAccessResponseDto;
import org.nistagram.followermicroservice.controller.dto.UserDto;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.exception.*;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.service.FollowService;
import org.nistagram.followermicroservice.service.UserService;
import org.nistagram.followermicroservice.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final FollowService followService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
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
//    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
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

    @GetMapping("/hasAccess/{follower}/{followee}")
    public ResponseEntity<HasAccessResponseDto> hasAccess(
            @PathVariable("follower") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String follower,
            @PathVariable("followee") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String followee) {
        try {
            followService.validateAccess(follower, followee);
            var result = new HasAccessResponseDto(true, "");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (FollowRequestDoesNotExistException e) {
            // TODO: log
            var result = new HasAccessResponseDto(false, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (FollowRequestNotApprovedException e) {
            // TODO: log
            var result = new HasAccessResponseDto(false, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            // TODO: log
            var result = new HasAccessResponseDto(false, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(new HasAccessResponseDto(false, "Something went wrong"), HttpStatus.BAD_REQUEST);
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
