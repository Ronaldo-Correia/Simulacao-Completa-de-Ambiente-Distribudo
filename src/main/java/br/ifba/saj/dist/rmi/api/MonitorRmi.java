package br.ifba.saj.dist.rmi.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitorRmi extends Remote {
    String ping() throws RemoteException;

    // Autentica e devolve um token de sessão (expira pelo TTL do SessionManager)
    String login(String user, String pass) throws RemoteException;

    // Exemplo de chamada protegida por token
    String checkStatus(int nodeId, String token) throws RemoteException;
}
