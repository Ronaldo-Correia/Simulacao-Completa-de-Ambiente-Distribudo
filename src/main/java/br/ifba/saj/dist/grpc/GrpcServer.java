package br.ifba.saj.dist.grpc;

import br.ifba.saj.dist.grpc.services.AuthServiceImpl;
import br.ifba.saj.dist.grpc.services.MonitorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    private Server server;

    public void start(int port) throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new AuthServiceImpl())
                .addService(new MonitorServiceImpl())
                .build()
                .start();

        System.out.println("🚀 gRPC server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("🔻 Shutting down gRPC server...");
            GrpcServer.this.stop();
        }));

        server.awaitTermination();
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
