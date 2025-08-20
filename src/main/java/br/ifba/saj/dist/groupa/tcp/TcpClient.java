package br.ifba.saj.dist.groupa.tcp;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;

import java.io.*;
import java.net.*;

public class TcpClient {
    private final String host;
    private final int port;
    private final LamportClock clock = new LamportClock();

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendMessage(String sender, String content) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Message msg = new Message(sender, content, clock.increment());
            out.writeObject(msg);

            Message reply = (Message) in.readObject();
            clock.update(reply.getTimestamp());

            System.out.printf("[TCP CLIENT] Reply from %s: %s (ts=%d)%n",
                    reply.getSender(), reply.getContent(), reply.getTimestamp());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
