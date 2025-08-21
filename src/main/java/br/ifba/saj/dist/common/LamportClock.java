package br.ifba.saj.dist.common;

public class LamportClock {
    private int time;
    private final int nodeId;

    // Construtor padrão (para casos sem nodeId)
    public LamportClock() {
        this.nodeId = -1;
        this.time = 0;
    }

    // Construtor com nodeId
    public LamportClock(int nodeId) {
        this.nodeId = nodeId;
        this.time = 0;
    }

    /** Evento interno ou envio de mensagem */
    public synchronized int increment() {
        time++;
        return time;
    }

    /** Retorna o valor atual do clock */
    public synchronized int getTime() {
        return time;
    }

    /** Recebe um evento externo (ex: mensagem) e ajusta o clock */
    public synchronized int receiveAction(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
        return time;
    }

    /** Atalho para ação local (igual a increment) */
    public synchronized int receiveAction() {
        return increment();
    }

    /** Alias para compatibilidade */
    public synchronized int update(int receivedTime) {
        return receiveAction(receivedTime);
    }
    /** Alias para increment (tick do relógio) */
public synchronized int tick() {
    return increment();
}

    @Override
    public synchronized String toString() {
        return "Node-" + nodeId + " | Clock=" + time;
    }
}
