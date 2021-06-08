package org.nistagram.followermicroservice.service;

public interface FollowService {
    void follow(String followeeUsername);

    void unfollow(String followeeUsername);

    void acceptFollowRequest(String followeeUsername);

    void rejectFollowRequest(String followeeUsername);

    void validateAccess(String follower, String followee);
}
