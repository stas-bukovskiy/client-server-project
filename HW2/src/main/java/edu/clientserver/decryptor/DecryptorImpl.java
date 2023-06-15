package edu.clientserver.decryptor;

import edu.clientserver.util.CRC16;
import edu.clientserver.message.Message;
import edu.clientserver.providers.ClientNumberProvider;
import edu.clientserver.providers.MessageNumberProvider;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;

import static edu.clientserver.util.PackageOffsets.*;
import static edu.clientserver.util.ParseUtil.*;

@Slf4j
public class DecryptorImpl implements Decryptor {

    private final MessageNumberProvider messageNumberProvider;
    private final ClientNumberProvider clientNumberProvider;
    private final BlockingQueue<Message> requestObjQueue;

    public DecryptorImpl(BlockingQueue<Message> requestObjQueue) {
        this.requestObjQueue = requestObjQueue;
        clientNumberProvider = ClientNumberProvider.get();
        messageNumberProvider = MessageNumberProvider.get();
    }

    @Override
    public void decrypt(byte[] messageBytes) {
        validate(messageBytes);

        int commandCode = getCommandCode(messageBytes);
        int userId = getSenderId(messageBytes);
        String msg = getMessage(messageBytes);

        Message messageObj = new Message(commandCode, userId, msg);
        log.info("decrypted bytes to message: <{}>", messageObj);

        try {
            requestObjQueue.put(messageObj);
            log.info("send encrypted message via requestObjQueue");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(byte[] bytes) {
        validateHeader(bytes);
        validateHeaderCRC16(bytes);
        validateMessageLength(bytes);
        validateBodyCRC16(bytes);
    }

    private void validateHeaderCRC16(byte[] bytes) {
        int headerCrc = twoBytesToInt(bytes, HEADER_CRC16_OFFSET);
        byte[] headerBytes = new byte[13];
        System.arraycopy(bytes, 0, headerBytes, 0, 13);
        if (headerCrc != CRC16.calculate(headerBytes))
            throw new IllegalArgumentException("Invalid header CRC16");
    }

    private void validateHeader(byte[] bytes) {
        if (bytes[PACKAGE_START_OFFSET] != 0x13)
            throw new IllegalArgumentException("Invalid package start: expected <13h>, but actual <" + bytes[PACKAGE_START_OFFSET] + ">");

        if (isClientNumberInvalid(bytes[CLIENT_NUMBER_OFFSET]))
            throw new IllegalArgumentException("Invalid client number");

        long messageNumber = bytesToLong(bytes, MESSAGE_NUMBER_OFFSET);
        if (isMessageNumberInvalid(messageNumber))
            throw new IllegalArgumentException("Invalid message number");
    }

    private void validateMessageLength(byte[] bytes) {
        long messageLength = getMassageLength(bytes);
        long expectedLength = COMMAND_CODE_OFFSET + messageLength + CRC16_LENGTH;
        if (bytes.length != expectedLength)
            throw new IllegalArgumentException("Invalid package length: expected <" + expectedLength + ">, actual <" + bytes.length + ">");
    }

    private void validateBodyCRC16(byte[] bytes) {
        long messageLength = getMassageLength(bytes);
        int bodyCrc = twoBytesToInt(bytes, (int) (COMMAND_CODE_OFFSET + messageLength));
        byte[] bodyBytes = new byte[(int) (messageLength)];
        System.arraycopy(bytes, COMMAND_CODE_OFFSET, bodyBytes, 0, (int) (messageLength));
        if (bodyCrc != CRC16.calculate(bodyBytes))
            throw new IllegalArgumentException("Invalid body CRC16");
    }

    private String getMessage(byte[] bytes) {
        int messageLength = (int) (getMassageLength(bytes) - 8);
        byte[] messageBytes = new byte[messageLength];
        System.arraycopy(bytes, MESSAGE_OFFSET, messageBytes, 0, messageLength);
        return new String(Base64.getDecoder().decode(messageBytes), StandardCharsets.UTF_8);
    }

    private int getCommandCode(byte[] bytes) {
        return bytesToInt(bytes, COMMAND_CODE_OFFSET);
    }

    private int getSenderId(byte[] bytes) {
        return bytesToInt(bytes, SENDER_ID_OFFSET);
    }

    private long getMassageLength(byte[] bytes) {
        return bytesToInt(bytes, MESSAGE_LENGTH_OFFSET);
    }


    private boolean isMessageNumberInvalid(long messageNumber) {
        return !messageNumberProvider.validate(messageNumber);
    }

    private boolean isClientNumberInvalid(byte clientNumber) {
        return !clientNumberProvider.validate(clientNumber);
    }

}
