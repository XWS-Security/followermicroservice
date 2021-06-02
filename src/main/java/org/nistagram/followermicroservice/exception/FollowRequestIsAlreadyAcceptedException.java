package org.nistagram.followermicroservice.exception;

public class FollowRequestIsAlreadyAcceptedException extends RuntimeException {
    public FollowRequestIsAlreadyAcceptedException() {
        super("Follow request has already been accepted.");
    }
}
