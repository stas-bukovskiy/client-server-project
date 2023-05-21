package edu.ukma.clientserverproject.protocol;

import edu.ukma.clientserverproject.providers.UserIdProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageSenderTest {

    private MessageSender sender;
    private int senderId;

    @BeforeEach
    void setUp() {
        sender = new MessageSender();
        senderId = UserIdProvider.get().provide();
    }

    @Test
    void testSendWithNullMessage_shouldThrowException() {
        Message invalidMessage = new Message(1, senderId, null);
        assertThrows(IllegalArgumentException.class, () -> sender.send(invalidMessage));
    }

    @Test
    void testSendWithInvalidSender_shouldThrowException() {
        int invalidUserId = senderId + 1;
        assertFalse(UserIdProvider.get().validate(invalidUserId));
        Message invalidMessage = new Message(1, invalidUserId, "...");
        assertThrows(IllegalArgumentException.class, () -> sender.send(invalidMessage));
    }
}