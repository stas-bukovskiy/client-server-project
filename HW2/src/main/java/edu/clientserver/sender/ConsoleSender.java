package edu.clientserver.sender;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public class ConsoleSender implements Sender {
    @Override
    public void sendMessage(byte[] message, InetAddress address) {
       log.info("sent response <{}>", message);
    }
}
