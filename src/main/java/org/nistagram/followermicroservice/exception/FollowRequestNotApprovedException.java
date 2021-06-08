package org.nistagram.followermicroservice.exception;

public class FollowRequestNotApprovedException extends RuntimeException {
    public FollowRequestNotApprovedException() {
        super("Follow request is not yet approved.");
    }
}
