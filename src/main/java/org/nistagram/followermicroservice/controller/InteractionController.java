package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.exception.*;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/interactions")
public class InteractionController {
    private final FollowService followService;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    @Autowired
    public InteractionController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/")
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

    @PutMapping("/acceptRequest")
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

    @PutMapping("/rejectRequest")
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
}
