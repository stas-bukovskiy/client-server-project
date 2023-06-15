package edu.clientserver;

import edu.clientserver.message.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class ReceiverWorkerTest {

    
    private ReceiverWorker receiverWorker;
    private BlockingQueue<Message> requestObjQueue;
    
    @BeforeEach
    void setUp() {
        requestObjQueue = new LinkedBlockingQueue<>();
        receiverWorker = new ReceiverWorker(requestObjQueue);
    }

    @Test
    void testReceiveMessage_requestObjQueueShouldContainMessage() throws InterruptedException {
        receiverWorker.receive();

        Thread.sleep(100);
        assertEquals(1, requestObjQueue.size());
    }

    @Test
    void testReceiveMessageNTimes_requestObjQueueShouldContainNMessages() throws InterruptedException {
        final int n = 10;

        for (int i = 0; i < n; i++) {
            receiverWorker.receive();
        }

        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> requestObjQueue.size() == n);
        assertEquals(n, requestObjQueue.size());
    }

    @AfterEach
    void tearDown() {
        receiverWorker.shutdown();
    }
}