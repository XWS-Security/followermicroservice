package org.nistagram.followermicroservice.logging;

public interface LoggerService {
    void logCreateUser(String username);

    void logCreateUserSuccess(String username);

    void logCreateUserFail(String username, String reason);

    void logUpdateUser(String username, String oldUsername);

    void logUpdateUserSuccess(String username, String oldUsername);

    void logUpdateUserFail(String username, String oldUsername, String reason);
}
