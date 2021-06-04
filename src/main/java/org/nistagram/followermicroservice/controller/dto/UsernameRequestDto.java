package org.nistagram.followermicroservice.controller.dto;

import org.nistagram.followermicroservice.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class UsernameRequestDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String username;

    public UsernameRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
