package edu.clientserver.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private int commandCode;
    private int senderId;
    private String message;
}