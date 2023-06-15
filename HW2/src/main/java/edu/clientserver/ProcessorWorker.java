package edu.clientserver;

import edu.clientserver.message.Message;
import edu.clientserver.processor.Processor;
import edu.clientserver.processor.SimpleProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ProcessorWorker {

    private final ExecutorService executorService;

    private final Processor processor;
    private final BlockingQueue<Message> requestObjQueue;

    public ProcessorWorker(BlockingQueue<Message> requestObjQueue, BlockingQueue<Message> responseObjQueue) {
        this.requestObjQueue = requestObjQueue;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        processor = new SimpleProcessor(responseObjQueue);
    }

    public void process() {
        executorService.execute(() -> {
            try {
                log.info("worker ready to process a message...");
                processor.process(requestObjQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void shutdown() {
        log.info("shutting down...");
        executorService.shutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }

}
