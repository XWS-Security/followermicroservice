package org.nistagram.followermicroservice.exception;

public class FollowRequestDoesNotExistException extends RuntimeException {
    public FollowRequestDoesNotExistException() {
        super("Follow request does not exist.");
    }
}
