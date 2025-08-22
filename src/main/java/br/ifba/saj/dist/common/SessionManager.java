package br.ifba.saj.dist.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();
    private final long ttlMillis;

    // Construtor recebe o TTL em segundos
    public SessionManager(long ttlSeconds) {
        this.ttlMillis = ttlSeconds * 1000;
    }

    public String issue() {
        String token = UUID.randomUUID().toString();
        sessions.put(token, System.currentTimeMillis() + ttlMillis);
        return token;
    }

    public boolean isValid(String token) {
        if (token == null) return false;
        Long exp = sessions.get(token);
        if (exp == null) return false;
        if (System.currentTimeMillis() > exp) {
            sessions.remove(token);
            return false;
        }
        return true;
    }

    public void renew(String token) {
        if (sessions.containsKey(token)) {
            sessions.put(token, System.currentTimeMillis() + ttlMillis);
        }
    }
}
