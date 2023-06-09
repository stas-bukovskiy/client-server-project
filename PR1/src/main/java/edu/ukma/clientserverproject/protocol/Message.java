package edu.ukma.clientserverproject.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class Message {
    private int commandCode;
    private int senderId;
    private String message;
}
