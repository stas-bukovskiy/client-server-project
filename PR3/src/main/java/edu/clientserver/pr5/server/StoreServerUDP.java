package edu.clientserver.pr5.server;

import edu.clientserver.decryptor.Decryptor;
import edu.clientserver.decryptor.DecryptorImpl;
import edu.clientserver.encryptor.Encryptor;
import edu.clientserver.encryptor.EncryptorImpl;
import edu.clientserver.message.Message;
import edu.clientserver.processor.Processor;
import edu.clientserver.processor.SimpleProcessor;
import edu.clientserver.receiver.UDPReceiver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class StoreServerUDP {

    public static void main(String[] args) {
        StoreServerUDP server = new StoreServerUDP();
        server.run();
    }


    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(ServerProperties.UDP_SERVER_PORT);
            log.info("UDP Server is running on port " + ServerProperties.UDP_SERVER_PORT);

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                RequestHandler handler = new RequestHandler(socket, request);
                executor.submit(handler);

                byte[] acknowledgementBytes = "OK".getBytes();
                socket.send(new DatagramPacket(acknowledgementBytes, acknowledgementBytes.length,
                        request.getAddress(), request.getPort()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


@Slf4j
class RequestHandler implements Runnable {
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final BlockingQueue<byte[]> requestBytesQueue;
    private final BlockingQueue<Message> requestObjQueue;
    private final BlockingQueue<Message> responseObjQueue;
    private final UDPReceiver receiver;
    private final Decryptor decryptor;
    private final Processor processor;
    private final Encryptor encryptor;


    public RequestHandler(DatagramSocket socket, DatagramPacket request) {
        this.socket = socket;
        this.request = request;
        this.requestObjQueue = new LinkedBlockingQueue<>(1);
        requestBytesQueue = new LinkedBlockingQueue<>(1);
        responseObjQueue = new LinkedBlockingQueue<>(1);
        receiver = new UDPReceiver(request, requestBytesQueue);
        decryptor = new DecryptorImpl(requestObjQueue);
        processor = new SimpleProcessor(responseObjQueue);
        encryptor = new EncryptorImpl();
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


            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            boolean isAcknowledged = false;
            int retries = 0;

            while (!isAcknowledged && retries < ServerProperties.MAX_RETRIES) {
                DatagramPacket response = new DatagramPacket(encryptedResponse, encryptedResponse.length,
                        clientAddress, clientPort);
                socket.send(response);

                log.info("Response sent to {}: {}", clientAddress.getHostAddress(), clientPort);

                socket.setSoTimeout(ServerProperties.TIMEOUT);

                byte[] acknowledgmentBuffer = new byte[1024];
                DatagramPacket acknowledgmentPacket = new DatagramPacket(acknowledgmentBuffer,
                        acknowledgmentBuffer.length);
                try {
                    socket.receive(acknowledgmentPacket);
                    String acknowledgment = new String(acknowledgmentPacket.getData()).trim();
                    if (acknowledgment.equals("OK")) {
                        isAcknowledged = true;
                        log.info("Received acknowledgment from {}:{} ",
                                acknowledgmentPacket.getAddress().getHostAddress(),
                                acknowledgmentPacket.getPort());
                    }
                } catch (IOException e) {
                    log.warn("Acknowledgment not received. Retrying...");
                    retries++;
                }
            }

            if (!isAcknowledged) {
                log.error("Response not acknowledged after " + ServerProperties.MAX_RETRIES + " retries.");
            }

            // Reset socket timeout to infinity
            socket.setSoTimeout(0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}