package edu.ukma.clientserverproject.protocol;

import edu.ukma.clientserverproject.providers.ClientNumberProvider;
import edu.ukma.clientserverproject.providers.MessageNumberProvider;
import edu.ukma.clientserverproject.providers.UserIdProvider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static edu.ukma.clientserverproject.Util.*;
import static edu.ukma.clientserverproject.protocol.PackageOffsets.*;

public class MessageSender {

    private final MessageNumberProvider messageNumberProvider = MessageNumberProvider.get();
    private final byte clientNumber;

    public MessageSender() {
        clientNumber = ClientNumberProvider.get().provide();
    }


    public byte[] send(Message message) {
        validateMessage(message);

        byte[] messageBytes = Base64.getEncoder().encode(message.getMessage().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[MESSAGE_OFFSET + messageBytes.length + CRC16_LENGTH];

        createMessageHeader(bytes, messageBytes);
        createHeaderCRC16(bytes);

        createBodyMessage(bytes, message, messageBytes);
        createBodyCRC16(bytes, message, messageBytes);

        return bytes;
    }

    private void validateMessage(Message message) {
        if (message == null || message.getMessage() == null)
            throw new IllegalArgumentException("Message can not be null");
        if (!UserIdProvider.get().validate(message.getSenderId()))
            throw new IllegalArgumentException("Invalid sender id");
    }

    private void createMessageHeader(byte[] bytes, byte[] messageBytes) {
        bytes[PACKAGE_START_OFFSET] = 0x13;
        bytes[CLIENT_NUMBER_OFFSET] = clientNumber;
        byte[] messageNumberBytes = longToBytes(messageNumberProvider.provide());
        System.arraycopy(messageNumberBytes, 0, bytes, MESSAGE_NUMBER_OFFSET, MESSAGE_NUMBER_LENGTH);
        System.arraycopy(intToBytes(messageBytes.length + 8), 0, bytes, MESSAGE_LENGTH_OFFSET, MESSAGE_LENGTH_LENGTH);
    }

    private void createHeaderCRC16(byte[] bytes) {
        byte[] headerBytes = new byte[13];
        System.arraycopy(bytes, 0, headerBytes, 0, 13);
        int headerCrc16 = CRC16.calculate(headerBytes);
        System.arraycopy(shortToBytes((short) headerCrc16), 0, bytes, HEADER_CRC16_OFFSET, CRC16_LENGTH);
    }

    private void createBodyMessage(byte[] bytes, Message message, byte[] messageBytes) {
        System.arraycopy(intToBytes(message.getCommandCode()), 0, bytes, COMMAND_CODE_OFFSET, COMMAND_CODE_LENGTH);
        System.arraycopy(intToBytes(message.getSenderId()), 0, bytes, SENDER_ID_OFFSET, SENDER_ID_LENGTH);
        System.arraycopy(messageBytes, 0, bytes, MESSAGE_OFFSET, messageBytes.length);
    }

    private void createBodyCRC16(byte[] bytes, Message message, byte[] messageBytes) {
        byte[] bodyBytes = new byte[messageBytes.length + COMMAND_CODE_LENGTH + SENDER_ID_LENGTH];
        System.arraycopy(intToBytes(message.getCommandCode()), 0, bodyBytes, 0, COMMAND_CODE_LENGTH);
        System.arraycopy(intToBytes(message.getSenderId()), 0, bodyBytes, COMMAND_CODE_LENGTH, SENDER_ID_LENGTH);
        System.arraycopy(messageBytes, 0, bodyBytes, COMMAND_CODE_LENGTH + SENDER_ID_LENGTH, messageBytes.length);
        int bodyCrc16 = CRC16.calculate(bodyBytes);
        System.arraycopy(shortToBytes((short) bodyCrc16), 0, bytes, MESSAGE_OFFSET + messageBytes.length, CRC16_LENGTH);
    }


}
