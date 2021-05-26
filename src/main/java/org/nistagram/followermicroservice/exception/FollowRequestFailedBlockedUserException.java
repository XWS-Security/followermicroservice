package org.nistagram.followermicroservice.exception;

public class FollowRequestFailedBlockedUserException extends RuntimeException {
    public FollowRequestFailedBlockedUserException() {
        super("Cannot follow blocked user. Please unblock the user first.");
    }
}
