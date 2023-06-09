package edu.ukma.clientserverproject.protocol;

import edu.ukma.clientserverproject.providers.ClientNumberProvider;
import edu.ukma.clientserverproject.providers.MessageNumberProvider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static edu.ukma.clientserverproject.Util.*;
import static edu.ukma.clientserverproject.protocol.PackageOffsets.*;

public class MessageReceiver {

    private final MessageNumberProvider messageNumberProvider = MessageNumberProvider.get();
    private final ClientNumberProvider calendarNameProvider = ClientNumberProvider.get();


    public Message receive(byte[] bytes) {
        validate(bytes);

        int commandCode = getCommandCode(bytes);
        int userId = getSenderId(bytes);
        String message = getMessage(bytes);
        return new Message(commandCode, userId, message);
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
        return !calendarNameProvider.validate(clientNumber);
    }


}
