package edu.clientserver.receiver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class TCPReceiver implements Receiver {

    private final Socket clientSocket;
    private final BlockingQueue<byte[]> requestBytesQueue;


    public TCPReceiver(Socket clientSocket, BlockingQueue<byte[]> requestBytesQueue) {
        this.clientSocket = clientSocket;
        this.requestBytesQueue = requestBytesQueue;
    }

    @Override
    public void receiveMessage() {
        try {
            BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            byte[] buffer = new byte[1024];
            int requestBytesLength = in.read(buffer);
            byte[] requestBytes = new byte[requestBytesLength];
            System.arraycopy(buffer, 0, requestBytes, 0, requestBytesLength);
            requestBytesQueue.put(requestBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
