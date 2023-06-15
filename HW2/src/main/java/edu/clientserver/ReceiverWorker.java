package edu.clientserver;

import edu.clientserver.decryptor.Decryptor;
import edu.clientserver.decryptor.DecryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.receiver.FakeReceiver;
import edu.clientserver.receiver.Receiver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class ReceiverWorker {

    private final ExecutorService executorService;
    private final Receiver receiver;
    private final Decryptor decryptor;
    private final BlockingQueue<byte[]> requestBytesQueue;

    public ReceiverWorker(BlockingQueue<Message> requestObjQueue) {
        requestBytesQueue = new LinkedBlockingQueue<>();
        receiver = new FakeReceiver(requestBytesQueue);
        decryptor = new DecryptorImpl(requestObjQueue);
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void receive() {
        executorService.execute(() -> {
            log.info("message receiving started..");
            try {
                receiver.receiveMessage();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        executorService.execute(() -> {
            log.info("worker ready to decrypt a message...");
            try {
                decryptor.decrypt(requestBytesQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void shutdown() {
        log.info(" shutting down...");
        executorService.shutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }
}
