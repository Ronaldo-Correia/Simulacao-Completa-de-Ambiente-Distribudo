package br.ifba.saj.dist.grpc.services;

import br.ifba.saj.dist.proto.AuthProto.AuthRequest;
import br.ifba.saj.dist.proto.AuthProto.AuthResponse;
import br.ifba.saj.dist.proto.AuthServiceGrpc;
import io.grpc.stub.StreamObserver;

public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {
    @Override
    public void authenticate(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        boolean success = "valid-token".equals(request.getToken());

        AuthResponse response = AuthResponse.newBuilder()
                .setSuccess(success)
                .setMessage(success ? "Token válido" : "Token inválido")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
