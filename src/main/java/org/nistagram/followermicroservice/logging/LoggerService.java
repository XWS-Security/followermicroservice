package org.nistagram.followermicroservice.logging;

public interface LoggerService {
    void logException(String message);

    void logValidationFailed(String message);

    void logTokenException(String message);

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

    void logFollowRequestFailed(String follower, String followed, String reason);

    void logFollowRequestApprovalSent(String follower, String followed);

    void logFollowRequestApprovalSuccess(String follower, String followed);

    void logFollowRequestApprovalFailedUserHasBlockedYou(String follower, String followed);

    void logFollowRequestApprovalFailedUserBlocked(String follower, String followed);

    void logFollowRequestApprovalFailed(String follower, String followed, String reason);

    void logFollowRequestRejectionSent(String follower, String followed);

    void logFollowRequestRejectionSuccess(String follower, String followed);

    void logFollowRequestRejectionFailedUserHasBlockedYou(String follower, String followed);

    void logFollowRequestRejectionFailedUserBlocked(String follower, String followed);

    void logFollowRequestRejectionFailed(String follower, String followed, String reason);

    void logUnfollowRequestSent(String follower, String followed);

    void logUnfollowRequestSuccess(String follower, String followed);

    void logUnfollowRequestFailedUserHasBlockedYou(String follower, String followed);

    void logUnfollowRequestFailedUserBlocked(String follower, String followed);

    void logUnfollowRequestFailed(String follower, String followed, String reason);

    void logValidateAccessRequestSent(String follower, String followed);

    void logValidateAccessRequestSuccess(String follower, String followed);

    void logValidateAccessRequestFail(String follower, String followed, String reason);
}
