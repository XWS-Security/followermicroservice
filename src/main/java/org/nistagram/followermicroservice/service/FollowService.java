package org.nistagram.followermicroservice.service;

public interface FollowService {
    void follow(String followerUsername, String followeeUsername);

    void unfollow(String followerUsername, String followeeUsername);

    void approveFollowRequest(String followerUsername, String followeeUsername);

    void rejectFollowRequest(String followerUsername, String followeeUsername);
}
