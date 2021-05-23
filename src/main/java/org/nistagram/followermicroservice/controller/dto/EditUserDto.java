package org.nistagram.followermicroservice.controller.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class EditUserDto implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String username;
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
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
