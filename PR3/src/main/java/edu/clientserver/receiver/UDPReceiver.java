package edu.clientserver.receiver;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver implements Receiver {

    private final DatagramPacket request;
    private final BlockingQueue<byte[]> requestBytesQueue;

    public UDPReceiver(DatagramPacket request, BlockingQueue<byte[]> requestBytesQueue) {
        this.request = request;
        this.requestBytesQueue = requestBytesQueue;
    }

    @Override
    public void receiveMessage() {
        try {
            requestBytesQueue.put(request.getData());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
