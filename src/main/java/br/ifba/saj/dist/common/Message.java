package br.ifba.saj.dist.common;

/**
 * Representa uma mensagem básica entre nós.
 */

import java.io.Serializable;

public class Message implements Serializable {
    private final String sender;
    private final String content;
    private final int timestamp;

    public Message(String sender, String content, int timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
    public int getTimestamp() { return timestamp; }
}