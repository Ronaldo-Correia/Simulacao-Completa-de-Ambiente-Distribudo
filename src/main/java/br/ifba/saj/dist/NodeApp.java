package br.ifba.saj.dist;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;
import br.ifba.saj.dist.grpc.services.AuthServiceImpl;
import br.ifba.saj.dist.grpc.services.MonitorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class NodeApp {

    private static final String MULTICAST_ADDR = "230.0.0.1";
    private static final int MULTICAST_PORT = 4446;

    private final int nodeId;
    private final String group;
    private final String token;
    private final LamportClock lamportClock;

    private Server grpcServer;

    public NodeApp(int nodeId, String group) {
        this.nodeId = nodeId;
        this.group = group;
        this.token = "node-" + nodeId + "-" + UUID.randomUUID();
        this.lamportClock = new LamportClock(nodeId);
    }

    public void start() throws Exception {
        System.out.println("🚀 Node " + nodeId + " | Grupo " + group + " | Token=" + token);

        // ---- Inicia gRPC ----
        startGrpcServer();

        // ---- Inicia TCP Server ----
        new Thread(this::startTcpServer).start();

        // ---- Inicia Multicast Server ----
        new Thread(this::startMulticastServer).start();

        // ---- Loop do console ----
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Digite mensagem (tcp/mc/exit): ");
            String cmd = sc.nextLine();

            if (cmd.equals("exit")) {
                break;
            } else if (cmd.equals("tcp")) {
                lamportClock.tick();
                Message msg = new Message(nodeId, "tcp", lamportClock.getTime());
                System.out.println(">> [TCP] (simulação) " + msg);
            } else if (cmd.equals("mc")) {
                lamportClock.tick();
                Message msg = new Message(nodeId, "mc", lamportClock.getTime());
                sendMulticast(msg);
            }
        }

        sc.close();
        stopGrpcServer();
    }

    // ---------- gRPC ----------
    private void startGrpcServer() throws IOException {
        int port = 8000 + nodeId;
        grpcServer = ServerBuilder.forPort(port)
                .addService(new AuthServiceImpl())
                .addService(new MonitorServiceImpl())
                .build()
                .start();

        System.out.println("✅ [gRPC] Servidor iniciado na porta " + port);

        new Thread(() -> {
            try {
                grpcServer.awaitTermination();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stopGrpcServer() {
        if (grpcServer != null) {
            grpcServer.shutdown();
            System.out.println("🛑 [gRPC] Servidor encerrado.");
        }
    }

    // ---------- TCP ----------
    private void startTcpServer() {
        int port = 7000 + nodeId;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("✅ [TCP] Servidor iniciado na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Aqui você trataria o cliente (ex: InputStream/OutputStream)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- Multicast ----------
    private void startMulticastServer() {
        try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
            InetAddress groupAddr = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(groupAddr);
            System.out.println("✅ [MC] Escutando em " + MULTICAST_ADDR + ":" + MULTICAST_PORT);

            byte[] buf = new byte[256];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());

                String[] parts = received.split(";", 3);
                if (parts.length == 3) {
                    String sender = parts[0];
                    String content = parts[1];
                    int receivedTime = Integer.parseInt(parts[2]);

                    lamportClock.receiveAction(receivedTime);

                    System.out.println("<< [MC] " + sender + ": " + content +
                                       " | Clock=" + lamportClock.getTime());
                } else {
                    System.out.println("<< [MC] Mensagem inválida: " + received);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMulticast(Message msg) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress groupAddr = InetAddress.getByName(MULTICAST_ADDR);
            String data = msg.getSender() + ";" + msg.getContent() + ";" + msg.getTimestamp();
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), groupAddr, MULTICAST_PORT);
            socket.send(packet);
            System.out.println(">> [MC] Enviado => " + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: NodeApp <id> <grupo>");
            return;
        }
        int nodeId = Integer.parseInt(args[0]);
        String group = args[1];

        NodeApp app = new NodeApp(nodeId, group);
        app.start();
    }
}
