package org.nistagram.followermicroservice.controller;

import org.modelmapper.ModelMapper;
import org.nistagram.followermicroservice.controller.dto.EditUserDto;
import org.nistagram.followermicroservice.controller.dto.HasAccessResponseDto;
import org.nistagram.followermicroservice.controller.dto.ResponseDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<ResponseDto> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            loggerService.logCreateUser(userDto.getUsername());
            userService.saveUser(modelMapper.map(userDto, User.class));
            loggerService.logCreateUserSuccess(userDto.getUsername());
            return new ResponseEntity<>(new ResponseDto(true, ""), HttpStatus.OK);
        } catch (UsernameAlreadyExistsException e) {
            loggerService.logCreateUserFail(userDto.getUsername(), e.getMessage());
            return new ResponseEntity<>(new ResponseDto(false, e.getMessage()), HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(new ResponseDto(false, "Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody @Valid UserDto userDto) {
        try {
            loggerService.logCreateUserReverted(userDto.getUsername());
            userService.deleteUser(modelMapper.map(userDto, User.class));
            loggerService.logCreateUserRevertedSuccess(userDto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logCreateUserRevertedFail(userDto.getUsername(), e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.BAD_REQUEST);
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

    @GetMapping("/hasAccess/{username}")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<HasAccessResponseDto> hasInteractionAccess(
            @PathVariable("username") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String username) {
        String currentUser = getCurrentlyLoggedUser().getUsername();
        try {
            loggerService.logValidateAccessRequestSent(currentUser, username);
            followService.validateInteractionAccess(username);
            var result = new HasAccessResponseDto(true, "");
            loggerService.logValidateAccessRequestSuccess(currentUser, username);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (FollowRequestDoesNotExistException | InvalidFollowRequestUserIsBlockedException | FollowRequestNotApprovedException e) {
            var result = new HasAccessResponseDto(false, e.getMessage());
            loggerService.logValidateAccessRequestFail(currentUser, username, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(new HasAccessResponseDto(false, "Something went wrong"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/hasViewAccess/{username}")
    public ResponseEntity<HasAccessResponseDto> hasViewAccess(
            @PathVariable("username") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String username) {
        try {
            followService.validateViewAccess(username);
            var result = new HasAccessResponseDto(true, "");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (FollowRequestDoesNotExistException | InvalidFollowRequestUserIsBlockedException | FollowRequestNotApprovedException e) {
            var result = new HasAccessResponseDto(false, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AnonymousUserCannotAccessPrivateProfileContentException e) {
            var result = new HasAccessResponseDto(false, e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
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

    private User getCurrentlyLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
