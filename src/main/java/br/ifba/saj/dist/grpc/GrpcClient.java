package br.ifba.saj.dist.grpc;

import br.ifba.saj.dist.proto.AuthProto.AuthRequest;
import br.ifba.saj.dist.proto.AuthProto.AuthResponse;
import br.ifba.saj.dist.proto.AuthServiceGrpc;
import br.ifba.saj.dist.proto.MonitorProto.StatusRequest;
import br.ifba.saj.dist.proto.MonitorProto.StatusResponse;
import br.ifba.saj.dist.proto.MonitorServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: GrpcClient <host> <nodeId> [token]");
            return;
        }

        String host = args[0];
        int nodeId = Integer.parseInt(args[1]);
        String token = args.length > 2 ? args[2] : "default-token";

        int port = 8000 + nodeId;

        // Cria o canal
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            // ---- 1) Chama AuthService ----
            AuthServiceGrpc.AuthServiceBlockingStub authStub = AuthServiceGrpc.newBlockingStub(channel);

            AuthRequest authRequest = AuthRequest.newBuilder()
                    .setToken(token)
                    .build();

            AuthResponse authResponse = authStub.authenticate(authRequest);

            if (authResponse.getSuccess()) {
                System.out.println("✅ Autenticado: " + authResponse.getMessage());

                // ---- 2) Chama MonitorService ----
                MonitorServiceGrpc.MonitorServiceBlockingStub monitorStub = MonitorServiceGrpc.newBlockingStub(channel);

                StatusRequest statusRequest = StatusRequest.newBuilder()
                        .setNodeId(nodeId)
                        .build();

                StatusResponse statusResponse = monitorStub.checkStatus(statusRequest);

                System.out.println("📊 Status do nó: " + statusResponse.getStatus());

            } else {
                System.out.println("❌ Falha na autenticação: " + authResponse.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }
}
