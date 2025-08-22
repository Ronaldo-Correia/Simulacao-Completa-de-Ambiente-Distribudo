package br.ifba.saj.dist.grpc.client;

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

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            // ---- Auth ----
            AuthServiceGrpc.AuthServiceBlockingStub auth = AuthServiceGrpc.newBlockingStub(channel);
            AuthRequest authReq = AuthRequest.newBuilder().setToken(token).build();
            AuthResponse authRes = auth.authenticate(authReq);
            System.out.println("Auth success=" + authRes.getSuccess() + " msg=" + authRes.getMessage());

            if (authRes.getSuccess()) {
                // ---- Monitor ----
                MonitorServiceGrpc.MonitorServiceBlockingStub mon = MonitorServiceGrpc.newBlockingStub(channel);

                StatusRequest sreq = StatusRequest.newBuilder()
                        .setNodeId(nodeId) 
                        .build();

                StatusResponse sres = mon.checkStatus(sreq);

                System.out.println("📊 Resposta do monitor: " + sres);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }
}
