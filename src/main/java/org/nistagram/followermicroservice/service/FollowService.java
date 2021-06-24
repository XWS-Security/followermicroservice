package org.nistagram.followermicroservice.service;

import javax.net.ssl.SSLException;

public interface FollowService {
    void follow(String followeeUsername, String token) throws SSLException;

    void unfollow(String followeeUsername, String token) throws SSLException;

    void acceptFollowRequest(String followeeUsername, String token) throws SSLException;

    void rejectFollowRequest(String followeeUsername);

    void validateInteractionAccess(String username);

    void validateViewAccess(String username);
}
