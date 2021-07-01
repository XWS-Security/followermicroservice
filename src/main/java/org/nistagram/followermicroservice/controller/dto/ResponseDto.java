package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class ResponseDto implements Serializable {
    private boolean success;
    private String message;

    public ResponseDto() {
    }

    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
