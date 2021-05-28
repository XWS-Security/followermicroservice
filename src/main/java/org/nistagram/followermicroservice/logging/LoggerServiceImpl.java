package org.nistagram.followermicroservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerServiceImpl implements LoggerService {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LoggerFactory.getLogger(parentClass);
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
        logger.error("Updated user failed: { 'username': {}, 'oldUsername': {}, 'reason': {}", username, oldUsername, reason);
    }
}
