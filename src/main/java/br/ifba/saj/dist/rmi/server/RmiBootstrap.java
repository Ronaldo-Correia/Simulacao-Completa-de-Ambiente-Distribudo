package br.ifba.saj.dist.rmi.server;

import br.ifba.saj.dist.common.SessionManager;
import java.util.Map;

public class RmiBootstrap {
    public static void main(String[] args) {
        try {
            SessionManager sessions = new SessionManager(60); // TTL de 60s
            Map<String,String> users = Map.of("ronaldo","ifba", "admin","admin123");

            MonitorRmiImpl monitor = new MonitorRmiImpl(sessions, users);

            try {
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                System.out.println("✅ [RMI] Registry criado na porta 1099");
            } catch (Exception e) {
                System.out.println("ℹ [RMI] Registry já existente: " + e.getMessage());
            }

            java.rmi.Naming.rebind("rmi://localhost/MonitorService", monitor);
            System.out.println("✅ [RMI] MonitorService registrado no RMI");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
