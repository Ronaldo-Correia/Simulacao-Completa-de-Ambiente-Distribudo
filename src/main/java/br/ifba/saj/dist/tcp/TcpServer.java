package br.ifba.saj.dist.tcp;

import br.ifba.saj.dist.common.LamportClock;
import java.io.*;
import java.net.*;

public class TcpServer implements Runnable {
    private final int nodeId;
    private final int port;
    private final LamportClock clock;
    private volatile boolean running = true;

    public TcpServer(int nodeId, LamportClock clock) {
        this.nodeId = nodeId;
        this.clock = clock;
        this.port = 7000 + nodeId;
    }

    public void shutdown() {
        running = false;
        // abrir um socket “fake” para destravar o accept
        try (Socket ignored = new Socket("localhost", port)) {} catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("✅ [TCP] Servidor iniciado na porta " + port);
            while (running) {
                Socket s = server.accept();
                new Thread(() -> handle(s)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ [TCP] Erro no servidor: " + e.getMessage());
        }
    }

    private void handle(Socket s) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            String line = in.readLine(); // formato: sender;text;timestamp
            if (line != null) {
                String[] p = line.split(";", 3);
                if (p.length == 3) {
                    int sender = Integer.parseInt(p[0]);
                    String text = p[1];
                    int ts = Integer.parseInt(p[2]);
                    clock.receiveAction(ts);
                    System.out.println("<< [TCP] de " + sender + ": " + text + " | Clock=" + clock.getTime());
                } else {
                    System.out.println("<< [TCP] Mensagem inválida: " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ [TCP] Handler: " + e.getMessage());
        }
    }
}
