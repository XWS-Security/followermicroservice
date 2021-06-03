package org.nistagram.followermicroservice.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
        super("User with selected username does not exist.");
    }

    public UserDoesNotExistException(String username) {
        super("User " + username + " does not exist.");
    }
}
