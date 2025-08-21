package br.ifba.saj.dist.common;

import java.io.*;
import java.net.*;

/**
 * Classe utilitária para envio de mensagens em ambiente distribuído.
 * Suporta comunicação intra-grupo (TCP) e inter-grupo (UDP Multicast).
 */
public class CommunicationHelper {

    // Porta base para comunicação TCP dos nós (ex: 7001, 7002...)
    private static final int BASE_TCP_PORT = 7000;

    // Configuração de multicast (intergrupos)
    private static final String MULTICAST_ADDR = "230.0.0.1";
    private static final int MULTICAST_PORT = 4446;

    /**
     * Envia uma mensagem TCP para um nó específico do grupo.
     *
     * @param targetNodeId ID do nó de destino
     * @param msg mensagem a ser enviada
     */
    public static void sendTCP(int targetNodeId, Message msg) {
        int port = BASE_TCP_PORT + targetNodeId;
        try (Socket socket = new Socket("localhost", port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject(msg);
            System.out.printf(">> [TCP] Enviado para Node-%d: %s%n", targetNodeId, msg);

        } catch (IOException e) {
            System.err.printf("!! Falha ao enviar TCP para Node-%d: %s%n", targetNodeId, e.getMessage());
        }
    }

    /**
     * Envia uma mensagem via Multicast UDP (todos os nós recebem).
     *
     * @param msg mensagem a ser enviada
     */
    public static void sendMulticast(Message msg) {
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDR);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);

            byte[] buf = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);

            socket.send(packet);
            System.out.printf(">> [MULTICAST] Enviado: %s%n", msg);

        } catch (IOException e) {
            System.err.printf("!! Falha ao enviar Multicast: %s%n", e.getMessage());
        }
    }
}
