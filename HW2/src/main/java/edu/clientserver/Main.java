package edu.clientserver;

import edu.clientserver.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("main started...");
        BlockingQueue<Message> requestObjQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Message> responseObjQueue = new LinkedBlockingQueue<>();

        ReceiverWorker receiverWorker = new ReceiverWorker(requestObjQueue);
        ProcessorWorker processorWorker = new ProcessorWorker(requestObjQueue, responseObjQueue);
        SenderWorker senderWorker = new SenderWorker(responseObjQueue);

        receiverWorker.receive();
        processorWorker.process();
        senderWorker.send();;

        receiverWorker.shutdown();
        processorWorker.shutdown();
        senderWorker.shutdown();
        while (!receiverWorker.isTerminated() || !processorWorker.isTerminated() || !senderWorker.isTerminated()) {
            Thread.yield();
        }
        log.info("main finished...");
    }
}