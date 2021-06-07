package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.controller.dto.FollowingNumbersDto;
import org.nistagram.followermicroservice.controller.dto.InteractionDto;
import org.nistagram.followermicroservice.data.model.User;
import org.nistagram.followermicroservice.exception.*;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.service.FollowService;
import org.nistagram.followermicroservice.service.ResourcesService;
import org.nistagram.followermicroservice.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping(value = "/interactions")
@Validated
public class InteractionController {
    private final FollowService followService;
    private final ResourcesService resourcesService;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public InteractionController(FollowService followService, ResourcesService resourcesService) {
        this.followService = followService;
        this.resourcesService = resourcesService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> follow(@RequestBody @Valid FollowRequestDto dto) {
        String username = getCurrentlyLoggedUser().getUsername();
        try {
            loggerService.logFollowRequestSent(username, dto.getUsername());
            followService.follow(dto.getUsername());
            return new ResponseEntity<>("Follow request processed", HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestFailedUserHasBlockedYou(username, dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestFailedUserBlocked(username, dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException e) {
            loggerService.logFollowRequestFailed(username, dto.getUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> unfollow(@RequestBody @Valid FollowRequestDto dto) {
        String username = getCurrentlyLoggedUser().getUsername();
        try {
            loggerService.logUnfollowRequestSent(username, dto.getUsername());
            followService.unfollow(dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logUnfollowRequestFailedUserHasBlockedYou(username, dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logUnfollowRequestFailedUserBlocked(username, dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | UserIsNotFollowedException e) {
            loggerService.logUnfollowRequestFailed(username, dto.getUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @PutMapping("/accept")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> acceptFollowRequest(@RequestBody @Valid FollowRequestDto dto) {
        String username = getCurrentlyLoggedUser().getUsername();
        try {
            loggerService.logFollowRequestApprovalSent(dto.getUsername(), username);
            followService.acceptFollowRequest(dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestApprovalFailedUserHasBlockedYou(dto.getUsername(), username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestApprovalFailedUserBlocked(dto.getUsername(), username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | FollowRequestIsAlreadyAcceptedException e) {
            loggerService.logFollowRequestApprovalFailed(dto.getUsername(), username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> rejectFollowRequest(@RequestBody @Valid FollowRequestDto dto) {
        String username = getCurrentlyLoggedUser().getUsername();
        try {
            loggerService.logFollowRequestRejectionSent(dto.getUsername(), username);
            followService.rejectFollowRequest(dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestRejectionFailedUserHasBlockedYou(dto.getUsername(), username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestRejectionFailedUserBlocked(dto.getUsername(), username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | FollowRequestIsAlreadyAcceptedException e) {
            loggerService.logFollowRequestRejectionFailed(dto.getUsername(), username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/waiting")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<List<InteractionDto>> getWaitingForApproval() {
        try {
            // TODO: log
            List<InteractionDto> result = resourcesService.getWaitingForApproval();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> getFollowingStatus(@PathVariable("username") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String username) {
        try {
            // TODO: log
            String result = resourcesService.getFollowingStatus(username);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/numbers/{username}")
    public ResponseEntity<FollowingNumbersDto> getFollowingStats(@PathVariable("username") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String username) {
        try {
            // TODO: log
            var result = new FollowingNumbersDto(resourcesService.getNumOfFollowers(username), resourcesService.getNumOfFollowing(username));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
