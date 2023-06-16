package edu.clientserver.client;

import edu.clientserver.decryptor.DecryptorImpl;
import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.providers.ClientNumberProvider;
import edu.clientserver.server.ServerProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class StoreClientTCP {
    private final Encryptor encryptor;
    private final DecryptorImpl decryptor;

    private final int clientId;

    public StoreClientTCP() {
        clientId = ClientNumberProvider.get().provide();
        encryptor = new EncryptorImpl();
        decryptor = new DecryptorImpl(null);
    }

    public static void main(String[] args) {
        final int THREADS_NUMBER = 10;
        final int MESSAGES_PER_THREAD_NUMBER = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

        for (int i = 0; i < THREADS_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                log.warn("started client #{}", finalI);
                StoreClientTCP clientTCP = new StoreClientTCP();
                for (int j = 0; j < MESSAGES_PER_THREAD_NUMBER; j++) {
                    clientTCP.sendMessage();
                }
            });
        }


        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.yield();
        }
        log.info("main finished...");
    }

    public void sendMessage() {
        int retryCount = 0;
        boolean connected = false;
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        while (!connected && retryCount < ClientProperties.MAX_RETRIES) {
            try {
                socket = new Socket(ServerProperties.SERVER_HOST, ServerProperties.SERVER_PORT);
                connected = true;

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                log.warn("Connection failed. Retrying...");
                retryCount++;
                try {
                    Thread.sleep(ClientProperties.RETRY_DELAY);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (connected) {
            assert inputStream != null;
            assert outputStream != null;
            try {
                String message = "Hello, server!";
                byte[] bytes = encryptor.encrypt(new Message(1, clientId, message));
                outputStream.write(bytes);

                byte[] buffer = new byte[1024];
                int requestBytesLength = inputStream.read(buffer);
                byte[] responseBytes = new byte[requestBytesLength];
                System.arraycopy(buffer, 0, responseBytes, 0, requestBytesLength);
                Message response = decryptor.decryptMessage(responseBytes);
                log.info("Received response: {}", response);

                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.warn("Failed to establish a connection after {} retry attempts", ClientProperties.MAX_RETRIES);
        }
    }

}
