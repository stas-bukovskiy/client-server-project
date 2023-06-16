package edu.clientserver.receiver;

import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.providers.UserIdProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class FakeReceiver implements Receiver {

    private final int clientId;
    private final Encryptor encryptor;
    private final BlockingQueue<byte[]> requestBytesQueue;

    public FakeReceiver(BlockingQueue<byte[]> requestBytesQueue) {
        this.requestBytesQueue = requestBytesQueue;
        clientId = UserIdProvider.get().provide();
        encryptor = new EncryptorImpl();
    }

    @Override
    public void receiveMessage() {
        final String message = "Hello world!!! Current time: " + System.currentTimeMillis();

        Message messageOnj = new Message(1, clientId, message);
        byte[] messageBytes = encryptor.encrypt(messageOnj);

        try {
            requestBytesQueue.put(messageBytes);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("received message <{}>", messageOnj);
    }
}
