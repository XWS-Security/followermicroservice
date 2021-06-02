package org.nistagram.followermicroservice.exception;

public class InvalidFollowRequestUserIsBlockedException extends RuntimeException {
    public InvalidFollowRequestUserIsBlockedException() {
        super("This user is blocked.");
    }
}
