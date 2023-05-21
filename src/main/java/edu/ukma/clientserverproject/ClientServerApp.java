package edu.ukma.clientserverproject;

import edu.ukma.clientserverproject.protocol.Message;
import edu.ukma.clientserverproject.protocol.MessageReceiver;
import edu.ukma.clientserverproject.protocol.MessageSender;

import java.util.Arrays;

public class ClientServerApp {
    public static void main(String[] args) {
        MessageReceiver receiver = new MessageReceiver();
        MessageSender sender = new MessageSender();

        Message message = new Message(1, 1, "some message to be sent");

        byte[] sentBytes = sender.send(message);
        System.out.println("sent: " + Arrays.toString(sentBytes));
        Message receivedMessage = receiver.receive(sentBytes);
        System.out.println("received: " + receivedMessage);

    }

}
