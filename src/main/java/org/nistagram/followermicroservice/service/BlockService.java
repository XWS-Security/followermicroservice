package org.nistagram.followermicroservice.service;

import javax.net.ssl.SSLException;

public interface BlockService {
    void block(String username, String token) throws SSLException;

    void unblock(String username);
}
