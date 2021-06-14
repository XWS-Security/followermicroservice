package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class FollowingStatusDto implements Serializable {
    private String following;
    private String notifications;

    public FollowingStatusDto() {
    }

    public FollowingStatusDto(String following, String notifications) {
        this.following = following;
        this.notifications = notifications;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "FollowingStatusDto{" +
                "following='" + following + '\'' +
                ", notifications='" + notifications + '\'' +
                '}';
    }
}
