package org.nistagram.followermicroservice.controller.dto;

import org.nistagram.followermicroservice.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class EditUserDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String username;
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String oldUsername;
    private boolean profilePrivate = false;

    public EditUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}
