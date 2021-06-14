package org.nistagram.followermicroservice.controller;

import org.nistagram.followermicroservice.controller.dto.FollowRequestDto;
import org.nistagram.followermicroservice.logging.LoggerService;
import org.nistagram.followermicroservice.logging.LoggerServiceImpl;
import org.nistagram.followermicroservice.security.TokenUtils;
import org.nistagram.followermicroservice.service.BlockService;
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
@RequestMapping(value = "/blocked")
@Validated
public class BlockController {
    private final BlockService blockService;
    private final TokenUtils tokenUtils;
    private final LoggerService loggerService = new LoggerServiceImpl(this.getClass());

    public BlockController(BlockService blockService, TokenUtils tokenUtils) {
        this.blockService = blockService;
        this.tokenUtils = tokenUtils;
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> block(@RequestBody @Valid FollowRequestDto dto, HttpServletRequest request) {
        try {
            blockService.block(dto.getUsername(), tokenUtils.getToken(request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unblock")
    @PreAuthorize("hasAuthority('NISTAGRAM_USER_ROLE')")
    public ResponseEntity<String> unblock(@RequestBody @Valid FollowRequestDto dto, HttpServletRequest request) {
        try {
            blockService.unblock(dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
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
