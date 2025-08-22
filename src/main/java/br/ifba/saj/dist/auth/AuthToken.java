package br.ifba.saj.dist.auth;


import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class AuthToken implements Serializable {
    private final String value;
    private final Instant expiresAt;

    public AuthToken(String owner, int ttlSeconds) {
        this.value = owner + "-" + UUID.randomUUID();
        this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
    }
    public String getValue() { return value; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
