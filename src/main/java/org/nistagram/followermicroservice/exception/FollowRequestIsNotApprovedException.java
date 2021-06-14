package org.nistagram.followermicroservice.exception;

public class FollowRequestIsNotApprovedException extends RuntimeException {
    public FollowRequestIsNotApprovedException() {
        super("Follow request is not approved.");
    }
}
