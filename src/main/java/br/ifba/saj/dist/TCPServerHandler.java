package br.ifba.saj.dist;

import br.ifba.saj.dist.common.Message;
import br.ifba.saj.dist.common.LamportClock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TCPServerHandler implements Runnable {
    private final Socket socket;
    private final LamportClock clock;

    public TCPServerHandler(Socket socket, LamportClock clock) {
        this.socket = socket;
        this.clock = clock;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Message msg = (Message) in.readObject();
            clock.receiveAction();
            System.out.println("<< [TCP] Recebido de " + msg.getSender() +
                    " => " + msg.getContent() +
                    " | Clock=" + clock.getTime());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
