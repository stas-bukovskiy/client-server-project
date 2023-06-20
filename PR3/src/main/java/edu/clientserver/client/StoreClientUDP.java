package edu.clientserver.client;

import edu.clientserver.decryptor.DecryptorImpl;
import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.pr5.server.ServerProperties;
import edu.clientserver.providers.ClientNumberProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class StoreClientUDP {

    private final int clientId;

    private final Encryptor encryptor;
    private final DecryptorImpl decryptor;

    public StoreClientUDP() {
        clientId = ClientNumberProvider.get().provide();
        encryptor = new EncryptorImpl();
        decryptor = new DecryptorImpl(null);
    }

    public static void main(String[] args) {
        final int THREADS_NUMBER = 1;
        final int MESSAGES_PER_THREAD_NUMBER = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

        for (int i = 0; i < THREADS_NUMBER; i++) {
            int finalI = i;
            executorService.execute(() -> {
                log.warn("started client #{}", finalI);
                StoreClientUDP clientTCP = new StoreClientUDP();
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
        try {
            DatagramSocket socket = new DatagramSocket();

            String message = "Hello, server!";
            byte[] bytes = encryptor.encrypt(new Message(1, clientId, message));

            InetAddress serverAddress = InetAddress.getByName(ServerProperties.SERVER_HOST);
            DatagramPacket request = new DatagramPacket(bytes, bytes.length, serverAddress, ServerProperties.UDP_SERVER_PORT);

            int retries = 0;
            boolean isAcknowledged = false;

            while (!isAcknowledged && retries < ClientProperties.MAX_RETRIES) {
                socket.send(request);

//                socket.setSoTimeout(ClientProperties.RETRY_DELAY);

                byte[] acknowledgmentBuffer = new byte[1024];
                DatagramPacket acknowledgmentPacket = new DatagramPacket(acknowledgmentBuffer,
                        acknowledgmentBuffer.length);
                try {
                    socket.receive(acknowledgmentPacket);
                    String acknowledgment = new String(acknowledgmentPacket.getData()).trim();
                    if (acknowledgment.equals("OK")) {
                        isAcknowledged = true;
                        log.info("Received acknowledgment from the server.");
                    }
                } catch (IOException e) {
                    log.warn("Acknowledgment not received. Retrying...");
                    retries++;
                }
            }

            if (!isAcknowledged) {
                log.error("Request not acknowledged after " + ClientProperties.MAX_RETRIES + " retries.");
            }

            // Reset socket timeout to infinity
            socket.setSoTimeout(0);

            socket.close(); // Close the socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
