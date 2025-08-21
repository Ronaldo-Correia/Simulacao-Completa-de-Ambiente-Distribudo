package br.ifba.saj.dist.grpc.services;

import br.ifba.saj.dist.proto.MonitorProto.StatusRequest;
import br.ifba.saj.dist.proto.MonitorProto.StatusResponse;
import br.ifba.saj.dist.proto.MonitorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class MonitorServiceImpl extends MonitorServiceGrpc.MonitorServiceImplBase {
    @Override
    public void checkStatus(StatusRequest request, StreamObserver<StatusResponse> responseObserver) {
        StatusResponse response = StatusResponse.newBuilder()
                .setStatus("Node " + request.getNodeId() + " está ativo")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
