package org.nistagram.followermicroservice.logging;

public interface LoggerService {
    void logException(String message);

    void logCreateUser(String username);

    void logCreateUserSuccess(String username);

    void logCreateUserFail(String username, String reason);

    void logUpdateUser(String username, String oldUsername);

    void logUpdateUserSuccess(String username, String oldUsername);

    void logUpdateUserFail(String username, String oldUsername, String reason);

    void logFollowRequestSent(String follower, String followed);

    void logFollowRequestSuccess(String follower, String followed);

    void logFollowRequestFailedUserHasBlockedYou(String follower, String followed);

    void logFollowRequestFailedUserBlocked(String follower, String followed);

    void logFollowRequestFailedUserDoesNotExist(String follower, String followed, String doesNotExist);
}
