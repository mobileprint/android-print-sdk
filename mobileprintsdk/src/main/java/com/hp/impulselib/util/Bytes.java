package com.hp.impulselib.util;

public class Bytes {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String toHex(byte[] bytes) {
        return toHex(bytes, 0, bytes.length);
    }

    public static String toHex(byte[] bytes, int offset, int length) {
        char[] hexChars = new char[length * 2];
        for (int j = offset; j < length + offset; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[(j - offset) * 2] = hexArray[v >>> 4];
            hexChars[(j - offset) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String toHex(int aByte) {
        return String.format("0x%02X", aByte);
    }

    public static byte toByte(Integer byteInteger, int defaultByte) {
        return byteInteger == null ? (byte)defaultByte : byteInteger.byteValue();
    }
}
