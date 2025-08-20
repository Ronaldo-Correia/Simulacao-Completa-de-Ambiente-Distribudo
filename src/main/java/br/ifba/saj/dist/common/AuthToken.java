package br.ifba.saj.dist.common;

/**
 * Token de autenticação para controle de acesso.
 */

import java.time.Instant;

public class AuthToken {
    private final String token;
    private final Instant expiry;

    public AuthToken(String token, long ttlSeconds) {
        this.token = token;
        this.expiry = Instant.now().plusSeconds(ttlSeconds);
    }

    public String getToken() { return token; }
    public boolean isExpired() { return Instant.now().isAfter(expiry); }
}
