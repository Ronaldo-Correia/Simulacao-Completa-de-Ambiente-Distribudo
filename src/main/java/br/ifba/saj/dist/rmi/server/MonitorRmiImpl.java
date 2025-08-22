package br.ifba.saj.dist.rmi.server;

import br.ifba.saj.dist.common.SessionManager;
import br.ifba.saj.dist.rmi.api.MonitorRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class MonitorRmiImpl extends UnicastRemoteObject implements MonitorRmi {

    private final SessionManager sessions;
    private final Map<String, String> users; // user -> pass

    public MonitorRmiImpl(SessionManager sessions, Map<String,String> users) throws RemoteException {
        super();
        this.sessions = sessions;
        this.users = users;
    }

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public String login(String user, String pass) throws RemoteException {
        String expected = users.get(user);
        if (expected != null && expected.equals(pass)) {
            return sessions.issue(); // gera token válido
        }
        return "unauthorized";
    }

    @Override
    public String checkStatus(int nodeId, String token) throws RemoteException {
        if (!sessions.isValid(token)) return "unauthorized";
        sessions.renew(token);
        return "OK: node=" + nodeId;
    }
}
