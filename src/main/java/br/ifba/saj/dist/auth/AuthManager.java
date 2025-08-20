package br.ifba.saj.dist.auth;

import br.ifba.saj.dist.common.AuthToken;

import java.util.*;

public class AuthManager {
    private final Map<String, AuthToken> sessions = new HashMap<>();
    private final long ttlSeconds;

    public AuthManager(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public AuthToken login(String userId) {
        String tokenStr = UUID.randomUUID().toString();
        AuthToken token = new AuthToken(tokenStr, ttlSeconds);
        sessions.put(userId, token);
        return token;
    }

    public boolean validate(String userId, String token) {
        AuthToken t = sessions.get(userId);
        return t != null && t.getToken().equals(token) && !t.isExpired();
    }
}
