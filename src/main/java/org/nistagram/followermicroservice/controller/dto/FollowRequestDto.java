package org.nistagram.followermicroservice.controller.dto;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class FollowRequestDto implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String followerUsername;
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,12}", message = "Username must be 2 to 12 characters long and can contain only letters, numbers and an underscore.")
    private String followeeUsername;

    public FollowRequestDto() {
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public String getFolloweeUsername() {
        return followeeUsername;
    }

    public void setFolloweeUsername(String followeeUsername) {
        this.followeeUsername = followeeUsername;
    }
}
