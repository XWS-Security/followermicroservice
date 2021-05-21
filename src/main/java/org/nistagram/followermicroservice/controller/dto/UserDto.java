package org.nistagram.followermicroservice.controller.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class UserDto implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,12}", message = "Username must be 6 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String username;
    private boolean profilePrivate = false;

    public UserDto() {
    }

    public UserDto(String username, boolean profilePrivate) {
        this.username = username;
        this.profilePrivate = profilePrivate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }
}
