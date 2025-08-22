package br.ifba.saj.dist.rmi.server;

import br.ifba.saj.dist.common.SessionManager;
import br.ifba.saj.dist.rmi.api.MonitorRmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RmiServer {
    public static void main(String[] args) {
        try {
            // sessões duram 60 segundos
            SessionManager sessions = new SessionManager(60);

            // usuários de teste (pode vir de arquivo/DB se quiser)
            Map<String,String> users = Map.of(
                "admin", "admin",
                "user1", "123",
                "ronaldo", "ifba"
            );

            MonitorRmiImpl obj = new MonitorRmiImpl(sessions, users);
            MonitorRmi stub = (MonitorRmi) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MonitorService", stub);

            System.out.println("[RMI] Servidor iniciado na porta 1099 (serviço: MonitorService)");

            // mantém vivo
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
