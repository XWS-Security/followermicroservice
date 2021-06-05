package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.controller.dto.InteractionDto;
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
        try {
            loggerService.logFollowRequestSent(dto.getFollowerUsername(), dto.getFolloweeUsername());
            followService.follow(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>("Follow request processed", HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestFailedUserHasBlockedYou(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestFailedUserBlocked(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException e) {
            loggerService.logFollowRequestFailed(dto.getFollowerUsername(), dto.getFolloweeUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> unfollow(@RequestBody @Valid FollowRequestDto dto) {
        try {
            loggerService.logUnfollowRequestSent(dto.getFollowerUsername(), dto.getFolloweeUsername());
            followService.unfollow(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logUnfollowRequestFailedUserHasBlockedYou(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logUnfollowRequestFailedUserBlocked(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | UserIsNotFollowedException e) {
            loggerService.logUnfollowRequestFailed(dto.getFollowerUsername(), dto.getFolloweeUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong.", HttpStatus.OK);
        }
    }

    @PutMapping("/accept")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> acceptFollowRequest(@RequestBody @Valid FollowRequestDto dto) {
        try {
            loggerService.logFollowRequestApprovalSent(dto.getFollowerUsername(), dto.getFolloweeUsername());
            followService.acceptFollowRequest(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestApprovalFailedUserHasBlockedYou(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestApprovalFailedUserBlocked(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | FollowRequestIsAlreadyAcceptedException e) {
            loggerService.logFollowRequestApprovalFailed(dto.getFollowerUsername(), dto.getFolloweeUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> rejectFollowRequest(@RequestBody @Valid FollowRequestDto dto) {
        try {
            loggerService.logFollowRequestRejectionSent(dto.getFollowerUsername(), dto.getFolloweeUsername());
            followService.rejectFollowRequest(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logFollowRequestRejectionFailedUserHasBlockedYou(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (FollowRequestFailedBlockedUserException e) {
            loggerService.logFollowRequestRejectionFailedUserBlocked(dto.getFollowerUsername(), dto.getFolloweeUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestDoesNotExistException | FollowRequestIsAlreadyAcceptedException e) {
            loggerService.logFollowRequestRejectionFailed(dto.getFollowerUsername(), dto.getFolloweeUsername(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/waiting/{username}")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<List<InteractionDto>> getWaitingForApproval(@PathVariable("username") @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE) String username) {
        try {
            // TODO: log
            List<InteractionDto> result = resourcesService.getWaitingForApproval(username);
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
}
