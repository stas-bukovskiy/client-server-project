package edu.ukma.clientserverproject.protocol;

import edu.ukma.clientserverproject.providers.UserIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageReceiverTest {

    private MessageReceiver receiver;
    private byte[] messageBytes;

    @BeforeEach
    void setUp() {
        receiver = new MessageReceiver();

        MessageSender sender = new MessageSender();
        Message validMessage = new Message(0,  UserIdProvider.get().provide(), "some random text");
        messageBytes = sender.send(validMessage);
    }

    @Test
    void testReceiveWithInvalidStart_shouldThrowException() {
        messageBytes[0] = 0x12;
        assertThrows(IllegalArgumentException.class, () -> receiver.receive(messageBytes));
    }

    @Test
    void testReceiveWithChangedData_shouldThrowException() {
        messageBytes[5] = 10;
        assertThrows(IllegalArgumentException.class, () -> receiver.receive(messageBytes));
    }

    @Test
    void testReceiveWithChangedCRC16_shouldThrowException() {
        messageBytes[messageBytes.length - 1] = 0;
        messageBytes[messageBytes.length - 2] = 0;
        assertThrows(IllegalArgumentException.class, () -> receiver.receive(messageBytes));
    }
}