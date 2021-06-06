package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class FollowingNumbersDto implements Serializable {
    int followers;
    int following;

    public FollowingNumbersDto() {
    }

    public FollowingNumbersDto(int followers, int following) {
        this.followers = followers;
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}
