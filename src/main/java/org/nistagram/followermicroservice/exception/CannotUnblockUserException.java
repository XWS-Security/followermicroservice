package org.nistagram.followermicroservice.exception;

public class CannotUnblockUserException extends RuntimeException {
    public CannotUnblockUserException() {
        super("User cannot be unblocked.");
    }
}
