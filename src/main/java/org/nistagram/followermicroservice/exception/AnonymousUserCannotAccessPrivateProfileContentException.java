package org.nistagram.followermicroservice.exception;

public class AnonymousUserCannotAccessPrivateProfileContentException extends RuntimeException {
    public AnonymousUserCannotAccessPrivateProfileContentException() {
        super("Anonymous user cannot access the content of a private profile.");
    }
}
