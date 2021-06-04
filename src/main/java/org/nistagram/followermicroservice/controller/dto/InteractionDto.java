package org.nistagram.followermicroservice.controller.dto;

import java.io.Serializable;

public class InteractionDto implements Serializable {
    private String follower;
    private String status;

    public InteractionDto() {
    }

    public InteractionDto(String follower, String status) {
        this.follower = follower;
        this.status = status;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
