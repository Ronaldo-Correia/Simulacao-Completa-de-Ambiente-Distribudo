package br.ifba.saj.dist.rmi.client;

import br.ifba.saj.dist.rmi.api.MonitorRmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {
    private final MonitorRmi stub;
    private final String user;
    private final String pass;

    public RmiClient(String host, String user, String pass) throws Exception {
        this.user = user;
        this.pass = pass;
        Registry registry = LocateRegistry.getRegistry(host, 1099);
        this.stub = (MonitorRmi) registry.lookup("MonitorService");
    }

    public String login() throws Exception {
        return stub.login(user, pass);
    }

    public String checkStatus(int nodeId, String token) throws Exception {
        return stub.checkStatus(nodeId, token);
    }

    public String ping() throws Exception {
        return stub.ping();
    }
}
