package edu.clientserver.sender;

import java.net.InetAddress;

public interface Sender {
    void sendMessage(byte[] message, InetAddress address);
}
