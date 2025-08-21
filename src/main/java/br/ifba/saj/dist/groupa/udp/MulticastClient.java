package br.ifba.saj.dist.groupa.udp;

import br.ifba.saj.dist.common.LamportClock;
import br.ifba.saj.dist.common.Message;

import java.io.*;
import java.net.*;

public class MulticastClient {
    private final String groupAddress;
    private final int port;
    private final LamportClock clock = new LamportClock();

    public MulticastClient(String groupAddress, int port) {
        this.groupAddress = groupAddress;
        this.port = port;
    }

    public void sendMessage(String sender, String content) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(groupAddress);

            Message msg = new Message(sender, content, clock.increment());

            // Serializa o objeto
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);
            oos.flush();
            byte[] data = baos.toByteArray();

            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);

            System.out.printf("[MULTICAST CLIENT] Sent to %s:%d → %s (ts=%d)%n",
                    groupAddress, port, content, msg.getTimestamp());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
