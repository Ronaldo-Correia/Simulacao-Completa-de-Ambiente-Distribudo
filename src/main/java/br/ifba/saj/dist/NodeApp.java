package br.ifba.saj.dist;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;
import br.ifba.saj.dist.common.SessionManager;
import br.ifba.saj.dist.grpc.services.AuthServiceImpl;
import br.ifba.saj.dist.grpc.services.MonitorServiceImpl;
import br.ifba.saj.dist.rmi.client.RmiClient;
import br.ifba.saj.dist.rmi.server.MonitorRmiImpl;
import br.ifba.saj.dist.tcp.TcpClient;
import br.ifba.saj.dist.tcp.TcpServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class NodeApp {

    private static final String MULTICAST_ADDR = "230.0.0.1";
    private static final int MULTICAST_PORT = 4446;

    private final int nodeId;
    private final String group;
    private final String token;
    private final LamportClock lamportClock;

    private Server grpcServer;
    private TcpServer tcpServer;
    private TcpClient tcpClient;
    private RmiClient rmiClient;

    public NodeApp(int nodeId, String group) {
        this.nodeId = nodeId;
        this.group = group;
        this.token = "node-" + nodeId + "-" + UUID.randomUUID();
        this.lamportClock = new LamportClock(nodeId);
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
            } catch (InterruptedException ignored) {}
        }, "grpc-await-" + nodeId).start();
    }

    private void stopGrpcServer() {
        if (grpcServer != null) {
            grpcServer.shutdown();
            System.out.println("🛑 [gRPC] Servidor encerrado.");
        }
    }

    // ---------- TCP ----------
    private void startTcpServer() {
        tcpServer = new TcpServer(nodeId, lamportClock);
        new Thread(tcpServer, "tcp-server-" + nodeId).start();
    }

    // ---------- Multicast ----------
    private void startMulticastServer() {
        try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
            InetAddress groupAddr = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(groupAddr);
            System.out.println("✅ [MC] Escutando em " + MULTICAST_ADDR + ":" + MULTICAST_PORT);

            byte[] buf = new byte[512];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                String[] parts = received.split(";", 3);
                if (parts.length == 3) {
                    String sender = parts[0];
                    String content = parts[1];
                    int receivedTime;
                    try {
                        receivedTime = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("<< [MC] Timestamp inválido: " + parts[2]);
                        continue;
                    }

                    lamportClock.receiveAction(receivedTime);
                    System.out.println("<< [MC] " + sender + ": " + content +
                                       " | Clock=" + lamportClock.getTime());
                } else {
                    System.out.println("<< [MC] Mensagem inválida: " + received);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ [MC] Erro: " + e.getMessage());
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
            System.err.println("❌ [MC] Falha ao enviar: " + e.getMessage());
        }
    }

    // ---------- RMI ----------
private void startRmiServerIfMaster() {
    if (nodeId == 1) { // só o node master cria o servidor RMI
        try {
            Map<String,String> users = Map.of("ronaldo","ifba", "admin","admin123");
            SessionManager sessions = new SessionManager(60); // TTL de 60s

            MonitorRmiImpl monitor = new MonitorRmiImpl(sessions, users);

            try {
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                System.out.println("✅ [RMI] Registry criado na porta 1099");
            } catch (Exception e) {
                System.out.println("ℹ [RMI] Registry já existente: " + e.getMessage());
            }

            java.rmi.Naming.rebind("rmi://localhost/MonitorService", monitor);
            System.out.println("✅ [RMI] MonitorService registrado no RMI");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


private void startRmiClient() {
    try {
        this.rmiClient = new RmiClient("localhost", "ronaldo", "ifba");
        System.out.println("✅ [RMI] Cliente inicializado e conectado ao servidor RMI");
    } catch (Exception e) {
        System.err.println("❌ [RMI] Falha ao conectar ao servidor RMI: " + e.getMessage());
        e.printStackTrace();
    }
}

private String rmiLogin() {
    try {
        String token = rmiClient.login();
        System.out.println("✅ [RMI] Login token: " + token);
        return token;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

private String rmiCheckStatus(int targetNodeId, String token) {
    try {
        return rmiClient.checkStatus(targetNodeId, token);
    } catch (Exception e) {
        e.printStackTrace();
        return "erro";
    }
}


    // ---------- Start Node ----------
    public void start() throws Exception {
        System.out.println("🚀 Node " + nodeId + " | Grupo " + group + " | Token=" + token);

        // gRPC
        startGrpcServer();

        // TCP
        startTcpServer();
        tcpClient = new TcpClient(lamportClock);

        // Multicast
        new Thread(this::startMulticastServer, "mc-listener-" + nodeId).start();

        // RMI
        startRmiServerIfMaster();  // Exporta serviço apenas no Node 1
        Thread.sleep(500);          // Espera Registry ficar pronto
        startRmiClient();           // Conecta cliente

        // Console loop
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Digite comando (tcp <id> <msg> | mc <msg> | login | status | exit): ");
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("exit")) break;

                if (line.startsWith("tcp")) {
                    String[] p = line.split("\\s+", 3);
                    if (p.length < 2) continue;
                    int targetId = Integer.parseInt(p[1]);
                    String text = (p.length == 3) ? p[2] : "hello";
                    lamportClock.tick();
                    tcpClient.sendToNode(nodeId, targetId, text);
                    continue;
                }

                if (line.startsWith("mc")) {
                    String text = line.length() > 3 ? line.substring(3).trim() : "mc";
                    lamportClock.tick();
                    sendMulticast(new Message(nodeId, text, lamportClock.getTime()));
                    continue;
                }

                if (line.equalsIgnoreCase("login")) {
                    rmiLogin();
                    continue;
                }

                if (line.equalsIgnoreCase("status")) {
                    if (rmiClient != null) {
                        System.out.println("📊 Status via RMI: " +
                                rmiClient.checkStatus(nodeId, rmiLogin()));
                    }
                    continue;
                }

                System.out.println("Comando não reconhecido.");
            }
        }

        stopGrpcServer();
        if (tcpServer != null) tcpServer.shutdown();
        System.out.println("✅ Node " + nodeId + " finalizado.");
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
