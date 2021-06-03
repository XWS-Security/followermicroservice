package org.nistagram.followermicroservice.exception;

public class UserIsNotFollowedException extends RuntimeException {
    public UserIsNotFollowedException() {
        super("Cannot unfollow user that is not followed.");
    }
}
