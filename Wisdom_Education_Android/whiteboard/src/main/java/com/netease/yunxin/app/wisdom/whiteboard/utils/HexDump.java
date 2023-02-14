package com.netease.yunxin.app.wisdom.whiteboard.utils;

public class HexDump {

    private static final char m_hexCodes[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final int m_shifts[] = {60, 56, 52, 48, 44, 40, 36, 32, 28,
            24, 20, 16, 12, 8, 4, 0};

    private static String toHex(final long value, final int digitNum) {
        StringBuilder result = new StringBuilder(digitNum);

        for (int j = 0; j < digitNum; j++) {
            int index = (int) ((value >> m_shifts[j + (16 - digitNum)]) & 15);
            result.append(m_hexCodes[index]);
        }

        return result.toString();
    }

    public static String toHex(final byte value) {
        return toHex(value, 2);
    }

    public static String toHex(final byte[] value) {
        return toHex(value, 0, value.length);
    }

    public static String toHex(final byte[] value, final int offset,
                               final int length) {
        StringBuilder retVal = new StringBuilder();

        int end = offset + length;
        for (int x = offset; x < end; x++)
            retVal.append(toHex(value[x]));

        return retVal.toString();
    }
}
