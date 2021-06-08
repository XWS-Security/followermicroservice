package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class HasAccessResponseDto implements Serializable {
    private boolean accessAllowed;
    private String message;

    public HasAccessResponseDto() {
    }

    public HasAccessResponseDto(boolean accessAllowed, String message) {
        this.accessAllowed = accessAllowed;
        this.message = message;
    }

    public boolean isAccessAllowed() {
        return accessAllowed;
    }

    public void setAccessAllowed(boolean accessAllowed) {
        this.accessAllowed = accessAllowed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
