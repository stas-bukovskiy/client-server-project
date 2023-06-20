package edu.clientserver.pr5.server;

import edu.clientserver.decryptor.Decryptor;
import edu.clientserver.decryptor.DecryptorImpl;
import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.processor.Processor;
import edu.clientserver.processor.SimpleProcessor;
import edu.clientserver.receiver.TCPReceiver;
import edu.clientserver.sender.Sender;
import edu.clientserver.sender.TCPSenderImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class StoreServerTCP {

    private final ExecutorService executorService;

    public StoreServerTCP() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static void main(String[] args) {
        StoreServerTCP server = new StoreServerTCP();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(ServerProperties.SERVER_PORT)) {
            log.info("Server started on port: {}", ServerProperties.SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RequestHandler implements Runnable {
        private final Socket clientSocket;
        private final BlockingQueue<byte[]> requestBytesQueue;
        private final BlockingQueue<Message> requestObjQueue;
        private final BlockingQueue<Message> responseObjQueue;
        private final TCPReceiver receiver;
        private final Decryptor decryptor;
        private final Processor processor;
        private final Encryptor encryptor;
        private final Sender sender;

        private RequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.requestObjQueue = new LinkedBlockingQueue<>(1);
            requestBytesQueue = new LinkedBlockingQueue<>(1);
            responseObjQueue = new LinkedBlockingQueue<>(1);
            receiver = new TCPReceiver(clientSocket, requestBytesQueue);
            decryptor = new DecryptorImpl(requestObjQueue);
            processor = new SimpleProcessor(responseObjQueue);
            encryptor = new EncryptorImpl();
            sender = new TCPSenderImpl(clientSocket);
        }

        @Override
        public void run() {
            try {
                receiver.receiveMessage();
                decryptor.decrypt(requestBytesQueue.take());
                log.info("worker ready to process a message...");
                processor.process(requestObjQueue.take());
                log.info("worker ready to encrypt response...");
                byte[] encryptedResponse;
                encryptedResponse = encryptor.encrypt(responseObjQueue.take());
                sender.sendMessage(encryptedResponse, null);
                clientSocket.close();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
