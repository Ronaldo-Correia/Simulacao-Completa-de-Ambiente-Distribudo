package br.ifba.saj.dist.tcp;

import br.ifba.saj.dist.common.LamportClock;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    private final LamportClock clock;

    public TcpClient(LamportClock clock) {
        this.clock = clock;
    }

    public void sendToNode(int fromNodeId, int targetNodeId, String text) {
        int port = 7000 + targetNodeId;
        clock.tick();
        String payload = fromNodeId + ";" + text + ";" + clock.getTime();
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(payload);
            System.out.println(">> [TCP] -> " + targetNodeId + " | " + payload);
        } catch (Exception e) {
            System.err.println("❌ [TCP] Falha ao enviar: " + e.getMessage());
        }
    }
}
