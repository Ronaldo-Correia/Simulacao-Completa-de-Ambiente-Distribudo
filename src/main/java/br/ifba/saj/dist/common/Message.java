package br.ifba.saj.dist.common;

import java.io.Serializable;

public class Message implements Serializable {
    private final String sender;
    private final String content;
    private final int timestamp;

    // Construtor original
    public Message(String sender, String content, int timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Novo construtor para aceitar int
    public Message(int senderId, String content, int timestamp) {
        this.sender = "Node-" + senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
    public int getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}
