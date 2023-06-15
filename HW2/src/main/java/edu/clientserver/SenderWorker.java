package edu.clientserver;

import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.sender.ConsoleSender;
import edu.clientserver.sender.Sender;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SenderWorker {

    private final ExecutorService executorService;

    private final Encryptor encryptor;
    private final Sender sender;
    private final BlockingQueue<Message> responseObjQueue;

    public SenderWorker(BlockingQueue<Message> responseObjQueue) {
        this.responseObjQueue = responseObjQueue;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        encryptor = new EncryptorImpl();
        sender = new ConsoleSender();
    }

    public void send() {
        executorService.execute(() -> {
            try {
                log.info("worker ready to encrypt response...");
                byte[] encryptedResponse = encryptor.encrypt(responseObjQueue.take());
                sender.sendMessage(encryptedResponse, null);
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
