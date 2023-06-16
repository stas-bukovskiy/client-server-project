package edu.clientserver.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPSenderImpl implements Sender {
    private final Socket clientSocket;

    public TCPSenderImpl(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void sendMessage(byte[] message, InetAddress address) {
        try {
            try (OutputStream out = clientSocket.getOutputStream()) {
                out.write(message);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
