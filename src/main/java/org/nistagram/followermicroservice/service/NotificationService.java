package org.nistagram.followermicroservice.service;

import javax.net.ssl.SSLException;

public interface NotificationService {
    void mute(String username, String token) throws SSLException;

    void unmute(String username, String token) throws SSLException;
}
