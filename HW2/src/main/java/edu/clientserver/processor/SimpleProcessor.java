package edu.clientserver.processor;

import edu.clientserver.message.Message;
import edu.clientserver.providers.UserIdProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class SimpleProcessor implements Processor {

    private final int serverId;
    private final BlockingQueue<Message> responseObjQueue;

    public SimpleProcessor(BlockingQueue<Message> responseObjQueue) {
        this.responseObjQueue = responseObjQueue;
        serverId = UserIdProvider.get().provide();;
    }

    @Override
    public void process(Message message) {
        Message response = new Message(0, serverId, "ok");

        log.info("processed message <{}>: {}", message, response);

        try {
            responseObjQueue.put(response);
            log.info("send response <{}> via responseObjQueue", response);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
