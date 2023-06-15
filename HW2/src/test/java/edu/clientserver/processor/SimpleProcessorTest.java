package edu.clientserver.processor;

import edu.clientserver.message.Message;
import edu.clientserver.providers.UserIdProvider;
import edu.clientserver.util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class SimpleProcessorTest {

    private BlockingQueue<Message> responseObjQueue;
    private SimpleProcessor simpleProcessor;

    @BeforeEach
    void setUp() {
        responseObjQueue = new LinkedBlockingQueue<>();
        simpleProcessor = new SimpleProcessor(responseObjQueue);
    }

    @Test
    void process() throws InterruptedException {
        Message message = new Message(-1, -1, RandomUtil.randomString(10));

        simpleProcessor.process(message);

        assertEquals(1, responseObjQueue.size());
        assertTrue(UserIdProvider.get().validate(responseObjQueue.take().getSenderId()));
    }
}