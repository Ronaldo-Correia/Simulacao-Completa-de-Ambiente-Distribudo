package br.ifba.saj.dist.groupa.udp;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;

import java.io.*;
import java.net.*;

public class MulticastServer implements Runnable {
    private final String groupAddress;
    private final int port;
    private final LamportClock clock = new LamportClock();
    private volatile boolean running = true;

    public MulticastServer(String groupAddress, int port) {
        this.groupAddress = groupAddress;
        this.port = port;
    }

    public void start() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            InetAddress group = InetAddress.getByName(groupAddress);
            socket.joinGroup(group);
            System.out.println("[MULTICAST SERVER] Listening on " + groupAddress + ":" + port);

            while (running) {
                byte[] buf = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                try (ObjectInputStream ois =
                             new ObjectInputStream(new ByteArrayInputStream(packet.getData(),0,packet.getLength()))) {
                    Message msg = (Message) ois.readObject();
                    clock.update(msg.getTimestamp());
                    System.out.printf("[MULTICAST SERVER] From %s: %s (ts=%d)%n",
                            msg.getSender(), msg.getContent(), msg.getTimestamp());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            socket.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() { running = false; }

    @Override public void run() { start(); }
}
