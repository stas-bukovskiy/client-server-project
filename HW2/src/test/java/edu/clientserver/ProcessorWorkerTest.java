package edu.clientserver;

import edu.clientserver.message.Message;
import edu.clientserver.providers.UserIdProvider;
import edu.clientserver.util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorWorkerTest {

    private BlockingQueue<Message> requestObjQueue;
    private BlockingQueue<Message> responseObjQueue;
    private ProcessorWorker processorWorker;

    @BeforeEach
    void setUp() {
        requestObjQueue = new LinkedBlockingQueue<>();
        responseObjQueue = new LinkedBlockingQueue<>();

        processorWorker = new ProcessorWorker(requestObjQueue, responseObjQueue);
    }

    @AfterEach
    void tearDown() {
        processorWorker.shutdown();
    }

    @Test
    void testProcessOneMessage_responseObjQueueShouldContainResponse() throws InterruptedException {
        requestObjQueue.put(new Message(1, UserIdProvider.get().provide(), RandomUtil.randomString(10)));

        processorWorker.process();

        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> responseObjQueue.size() == 1);
        assertEquals("ok", responseObjQueue.take().getMessage());
    }

    @Test
    void testProcessNMessage_responseObjQueueShouldContainNResponses() throws InterruptedException {
        final int n = 10;
        for (int i = 0; i < n; i++) {
            requestObjQueue.put(new Message(1, UserIdProvider.get().provide(), RandomUtil.randomString(10)));
        }

        for (int i = 0; i < n; i++) {
            processorWorker.process();
        }

        await().atMost(5000, TimeUnit.MILLISECONDS).until(() -> responseObjQueue.size() == n);
        assertEquals(n, responseObjQueue.size());
    }
}