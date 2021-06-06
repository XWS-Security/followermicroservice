package org.nistagram.followermicroservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerServiceImpl implements LoggerService {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LoggerFactory.getLogger(parentClass);
    }

    @Override
    public void logException(String message) {
        logger.error("Unexpected exception: { message: {} }", message);
    }

    @Override
    public void logValidationFailed(String message) {
        logger.error("Validation failed: { message: {} }", message);
    }

    @Override
    public void logCreateUser(String username) {
        logger.info("Creating user: { 'username': {} }", username);
    }

    @Override
    public void logCreateUserSuccess(String username) {
        logger.info("Created user successfully: { 'username': {} }", username);
    }

    @Override
    public void logCreateUserFail(String username, String reason) {
        logger.error("Created user failed: { 'username': {}, 'reason': {} }", username, reason);
    }

    @Override
    public void logUpdateUser(String username, String oldUsername) {
        logger.info("Updating user: { 'username': {}, 'oldUsername': {} }", username, oldUsername);
    }

    @Override
    public void logUpdateUserSuccess(String username, String oldUsername) {
        logger.info("Updated user successfully: { 'username': {}, 'oldUsername': {} }", username, oldUsername);
    }

    @Override
    public void logUpdateUserFail(String username, String oldUsername, String reason) {
        logger.error("Updated user failed: { 'username': {}, 'oldUsername': {}, 'reason': {} }", username, oldUsername, reason);
    }

    @Override
    public void logFollowRequestSent(String follower, String followed) {
        logger.info("Follow request sent: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestSuccess(String follower, String followed) {
        logger.info("Follow request saved: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestFailedUserHasBlockedYou(String follower, String followed) {
        String reason = follower + " is BLOCKED";
        logFollowRequestFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestFailedUserBlocked(String follower, String followed) {
        String reason = followed + " is BLOCKED";
        logFollowRequestFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestFailed(String follower, String followed, String reason) {
        logger.error("Follow request failed: {'from': {}, 'to': {}, 'reason': {} }", follower, followed, reason);
    }

    @Override
    public void logFollowRequestApprovalSent(String follower, String followed) {
        logger.info("Follow request approval sent: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestApprovalSuccess(String follower, String followed) {
        logger.info("Follow request approved: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestApprovalFailedUserHasBlockedYou(String follower, String followed) {
        String reason = follower + " is BLOCKED";
        logFollowRequestApprovalFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestApprovalFailedUserBlocked(String follower, String followed) {
        String reason = followed + " is BLOCKED";
        logFollowRequestApprovalFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestApprovalFailed(String follower, String followed, String reason) {
        logger.error("Follow request approval failed: {'from': {}, 'to': {}, 'reason': {} }", follower, followed, reason);
    }

    @Override
    public void logFollowRequestRejectionSent(String follower, String followed) {
        logger.info("Follow request rejection sent: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestRejectionSuccess(String follower, String followed) {
        logger.info("Follow request rejected: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logFollowRequestRejectionFailedUserHasBlockedYou(String follower, String followed) {
        String reason = follower + " is BLOCKED";
        logFollowRequestApprovalFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestRejectionFailedUserBlocked(String follower, String followed) {
        String reason = followed + " is BLOCKED";
        logFollowRequestApprovalFailed(follower, followed, reason);
    }

    @Override
    public void logFollowRequestRejectionFailed(String follower, String followed, String reason) {
        logger.error("Follow request rejection failed: {'from': {}, 'to': {}, 'reason': {} }", follower, followed, reason);
    }

    @Override
    public void logUnfollowRequestSent(String follower, String followed) {
        logger.info("Unfollow request sent: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logUnfollowRequestSuccess(String follower, String followed) {
        logger.info("Unfollow successful, relationship deleted: {'from': {}, 'to': {} }", follower, followed);
    }

    @Override
    public void logUnfollowRequestFailedUserHasBlockedYou(String follower, String followed) {
        String reason = follower + " is BLOCKED";
        logUnfollowRequestFailed(follower, followed, reason);
    }

    @Override
    public void logUnfollowRequestFailedUserBlocked(String follower, String followed) {
        String reason = follower + " is BLOCKED";
        logUnfollowRequestFailed(follower, followed, reason);
    }

    @Override
    public void logUnfollowRequestFailed(String follower, String followed, String reason) {
        logger.error("Unfollow request failed: {'from': {}, 'to': {}, 'reason': {} }", follower, followed, reason);
    }
}
