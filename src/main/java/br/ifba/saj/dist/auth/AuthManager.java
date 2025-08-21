package br.ifba.saj.dist.auth;

import br.ifba.saj.dist.common.AuthToken;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    private static final ConcurrentHashMap<String, AuthToken> TOKENS = new ConcurrentHashMap<>();

    public static String generateToken(int nodeId) {
        AuthToken t = new AuthToken("node-" + nodeId, 60); // 60s
        TOKENS.put(t.getValue(), t);
        return t.getValue();
    }

    public static boolean isValid(String token) {
        AuthToken t = TOKENS.get(token);
        return t != null && !t.isExpired();
    }
}
