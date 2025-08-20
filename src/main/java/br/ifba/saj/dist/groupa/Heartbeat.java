package br.ifba.saj.dist.groupa;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;

public class Heartbeat {
    private final String host;
    private final int port;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Heartbeat(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::check, 0, 3, TimeUnit.SECONDS);
    }

    private void check() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            System.out.println("[HEARTBEAT] Node " + host + ":" + port + " is ALIVE");
        } catch (Exception e) {
            System.out.println("[HEARTBEAT] Node " + host + ":" + port + " is DOWN");
        }
    }
}
