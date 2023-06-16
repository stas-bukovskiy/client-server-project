package edu.clientserver.encryptor;

import edu.clientserver.message.Message;

public interface Encryptor {
    byte[] encrypt(Message message);
}
