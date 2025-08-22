package br.ifba.saj.dist.grpc.services;

import br.ifba.saj.dist.common.SessionManager;
import br.ifba.saj.dist.proto.AuthProto.AuthRequest;
import br.ifba.saj.dist.proto.AuthProto.AuthResponse;
import br.ifba.saj.dist.proto.AuthServiceGrpc;
import io.grpc.stub.StreamObserver;

public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {
    public static final SessionManager SESSIONS = new SessionManager(300); // 5 min

    @Override
    public void authenticate(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        boolean ok = request.getToken() != null && request.getToken().startsWith("node-");

        AuthResponse.Builder resp = AuthResponse.newBuilder();
        if (ok) {
            String sessionToken = SESSIONS.issue();
            resp.setSuccess(true)
                .setMessage("Token válido")
                .setSessionToken(sessionToken);
        } else {
            resp.setSuccess(false).setMessage("Token inválido");
        }

        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }
}
