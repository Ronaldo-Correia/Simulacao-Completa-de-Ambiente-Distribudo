package br.ifba.saj.dist.common;

public class LamportClock {
    private int time = 0;

    public synchronized int increment() {
        time++;
        return time;
    }

    public synchronized int update(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
        return time;
    }

    public synchronized int getTime() {
        return time;
    }
}