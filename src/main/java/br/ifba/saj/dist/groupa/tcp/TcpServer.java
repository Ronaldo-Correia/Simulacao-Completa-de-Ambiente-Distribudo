package br.ifba.saj.dist.groupa.tcp;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;

import java.io.*;
import java.net.*;

public class TcpServer implements Runnable {
    private final int port;
    private final int nodeId;
    private final LamportClock clock = new LamportClock();

    public TcpServer(int port, int nodeId) {
        this.port = port;
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("[TCP SERVER] Node-%d ouvindo na porta %d%n", nodeId, port);

            while (true) {
                Socket socket = serverSocket.accept();

                try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    Message msg = (Message) in.readObject();
                    clock.update(msg.getTimestamp());

                    System.out.printf("[TCP SERVER] Node-%d recebeu de %s: %s (ts=%d)%n",
                            nodeId, msg.getSender(), msg.getContent(), msg.getTimestamp());

                    Message reply = new Message("Node-" + nodeId, "ACK", clock.increment());
                    out.writeObject(reply);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
