package edu.clientserver.decryptor;

import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.providers.UserIdProvider;
import edu.clientserver.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecryptorImplTest {

    private BlockingQueue<Message> queue;
    private Decryptor decryptor;

    @BeforeEach
    void setUp() {
        queue = new LinkedBlockingQueue<>();
        decryptor = new DecryptorImpl(queue);
    }


    @Test
    void testDecryptIn10Threads10Times_queueShouldContain100Messages() throws InterruptedException {
        String message = RandomUtil.randomString(10);
        Message messageObj = new Message(1, UserIdProvider.get().provide(), message);
        Encryptor encryptor = new EncryptorImpl();
        byte[] messageBytes = encryptor.encrypt(messageObj);
        CountDownLatch latch = new CountDownLatch(1);

        decryptor.decrypt(messageBytes);
        latch.countDown();
        latch.await();

        assertEquals(1, queue.size());
        assertEquals(messageObj, queue.take());
    }


   
}