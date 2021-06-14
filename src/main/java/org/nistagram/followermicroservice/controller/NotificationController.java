package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.exception.FollowRequestFailedBlockedUserException;
import org.nistagram.followermicroservice.exception.FollowRequestIsNotApprovedException;
import org.nistagram.followermicroservice.exception.InvalidFollowRequestUserIsBlockedException;
import org.nistagram.followermicroservice.exception.UserDoesNotExistException;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.security.TokenUtils;
import org.nistagram.followermicroservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/notifications")
@Validated
public class NotificationController {
    private final NotificationService notificationService;
    private final TokenUtils tokenUtils;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    public NotificationController(NotificationService notificationService, TokenUtils tokenUtils) {
        this.notificationService = notificationService;
        this.tokenUtils = tokenUtils;
    }

    @PutMapping("/mute")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> mute(@RequestBody @Valid FollowRequestDto dto, HttpServletRequest request) {
        try {
            notificationService.mute(dto.getUsername(), tokenUtils.getToken(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestFailedBlockedUserException | FollowRequestIsNotApprovedException e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unmute")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> unmute(@RequestBody @Valid FollowRequestDto dto, HttpServletRequest request) {
        try {
            notificationService.unmute(dto.getUsername(), tokenUtils.getToken(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserDoesNotExistException | FollowRequestFailedBlockedUserException | FollowRequestIsNotApprovedException e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidFollowRequestUserIsBlockedException e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            loggerService.logException(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
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
