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
    public void logFollowRequestFailedUserDoesNotExist(String follower, String followed, String doesNotExist) {
        String reason = doesNotExist + " does not exist";
        logFollowRequestFailed(follower, followed, reason);
    }

    private void logFollowRequestFailed(String follower, String followed, String reason) {
        logger.error("Follow request failed: {'from': {}, 'to': {}, 'reason': {} }", follower, followed, reason);
    }
}
