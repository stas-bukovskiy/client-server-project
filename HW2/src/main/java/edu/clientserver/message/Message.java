package edu.clientserver.message;

import lombok.AllArgsConstructor;
import lombok.Data;


public class Message {
    private int commandCode;
    private int senderId;
    private String message;

    public Message(int commandCode, int senderId, String message) {
        this.commandCode = commandCode;
        this.senderId = senderId;
        this.message = message;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "commandCode=" + commandCode +
                ", senderId=" + senderId +
                ", message='" + message + '\'' +
                '}';
    }
}