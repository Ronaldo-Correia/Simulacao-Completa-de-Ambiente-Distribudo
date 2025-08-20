package br.ifba.saj.dist.groupa.tcp;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TcpServer {
    private final int port;
    private final LamportClock clock = new LamportClock();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[TCP SERVER] Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.submit(() -> handleClient(clientSocket));
            }
        }
    }

    private void handleClient(Socket socket) {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Message msg = (Message) in.readObject();
            clock.update(msg.getTimestamp());

            System.out.printf("[TCP SERVER] Received from %s: %s (ts=%d)%n",
                    msg.getSender(), msg.getContent(), msg.getTimestamp());

            // resposta com clock atualizado
            Message reply = new Message("Server", "ACK", clock.increment());
            out.writeObject(reply);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
