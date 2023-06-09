package edu.ukma.clientserverproject.protocol;

import edu.ukma.clientserverproject.providers.UserIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageSenderReceiverTest {

    private MessageSender sender;
    private MessageReceiver receiver;
    private int senderId;

    @BeforeEach
    void setUp() {
        sender = new MessageSender();
        receiver = new MessageReceiver();
        senderId = UserIdProvider.get().provide();
    }

    @Test
    void testSendMessage_shouldReceiveValidMessage() {
        Message message = new Message(1, senderId, "some message to be sent");

        byte[] sentBytes = sender.send(message);
        Message receivedMessage = receiver.receive(sentBytes);

        assertEquals(message, receivedMessage);
    }

    @Test
    void testSendEmptyMessage_shouldReceiveValidMessage() {
        Message message = new Message(1, senderId, "");

        byte[] sentBytes = sender.send(message);
        Message receivedMessage = receiver.receive(sentBytes);

        assertEquals(message, receivedMessage);
    }
}