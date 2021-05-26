package org.nistagram.followermicroservice.exception;

public class UserHasBlockedYouException extends RuntimeException {
    public UserHasBlockedYouException() {
        super("This action is not permitted you have been blocked by the user.");
    }
}
