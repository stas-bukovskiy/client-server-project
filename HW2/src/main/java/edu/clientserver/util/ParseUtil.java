package edu.clientserver.util;

import java.nio.ByteBuffer;

public final class ParseUtil {

    private ParseUtil() {}

    public static int twoBytesToInt(byte[] arr, int off) {
        return arr[off] << 8 & 0xFF00 | arr[off + 1] & 0xFF;
    }

    public static int bytesToInt(byte[] arr, int off) {
        byte[] bytes = new byte[4];
        System.arraycopy(arr, off, bytes, 0, 4);
        ByteBuffer buffer =ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getInt();
    }

    public static long bytesToLong(byte[] arr, int off) {
        byte[] bytes = new byte[8];
        System.arraycopy(arr, off, bytes, 0, 8);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(x);
        return buffer.array();
    }

    public static short bytesToShort(byte[] arr, int off) {
        byte[] bytes = new byte[2];
        System.arraycopy(arr, off, bytes, 0, 2);
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getShort();
    }

}
