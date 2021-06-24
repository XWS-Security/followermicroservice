package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class FollowingStatusDto implements Serializable {
    private String following;
    private String notifications;
    private boolean muted;
    private boolean blocked;

    public FollowingStatusDto() {
    }

    public FollowingStatusDto(String following, String notifications, boolean muted, boolean blocked) {
        this.following = following;
        this.notifications = notifications;
        this.muted = muted;
        this.blocked = blocked;
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

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
