package org.nistagram.followermicroservice.controller.dto;

import org.nistagram.followermicroservice.util.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class FollowRequestDto implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
    private String followerUsername;
    @NotNull
    @Pattern(regexp = Constants.USERNAME_PATTERN, message = Constants.USERNAME_INVALID_MESSAGE)
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
