package com.netease.util;

import java.io.IOException;
import java.io.StringReader;

public class HexDump {
    class HexTablifier {
        private int m_row = 8;

        private String m_pre = "";

        private String m_post = "\n";

        public HexTablifier() {}

        public HexTablifier(int row) {
            this(row, "", "\n");
        }

        public HexTablifier(int row, String pre) {
            this(row, pre, "\n");
        }

        public HexTablifier(int row, String pre, String post) {
            m_row = row;
            m_pre = pre;
            m_post = post;
        }

        public String format(String hex) {
            StringReader reader = new StringReader(hex);
            StringBuilder builder = new StringBuilder(hex.length() * 2);

            try {
                while (getHexLine(builder, reader)) {}
            } catch (IOException e) {
                // 不应该有异常出现。
            }

            return builder.toString();
        }

        private boolean getHexLine(StringBuilder builder, StringReader reader)
            throws IOException {
            StringBuilder lineBuilder = new StringBuilder();
            boolean result = true;

            for (int i = 0; i < m_row; i++) {
                result = getHexByte(lineBuilder, reader);

                if (result == false)
                    break;
            }

            if (lineBuilder.length() > 0) {
                builder.append(m_pre);
                builder.append(lineBuilder);
                builder.append(m_post);
            }

            return result;
        }

        private boolean getHexByte(StringBuilder builder, StringReader reader)
            throws IOException {
            char[] hexByte = new char[4];
            int bytesRead = reader.read(hexByte);

            if (bytesRead == -1)
                return false;

            builder.append(hexByte, 0, bytesRead);
            builder.append(" ");

            return bytesRead == 4;
        }
    }

    private static final char m_hexCodes[] = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final int m_shifts[] = { 60, 56, 52, 48, 44, 40, 36, 32, 28,
        24, 20, 16, 12, 8, 4, 0 };

    public static String tablify(byte[] bytes) {
        return (new HexDump()).new HexTablifier().format(HexDump.toHex(bytes));
    }

    public static String tablify(byte[] bytes, int row) {
        return (new HexDump()).new HexTablifier(row).format(HexDump
            .toHex(bytes));
    }

    public static String tablify(byte[] bytes, int row, String pre) {
        return (new HexDump()).new HexTablifier(row, pre).format(HexDump
            .toHex(bytes));
    }

    public static String tablify(String hex, int row, String pre, String post) {
        return (new HexDump()).new HexTablifier(row, pre, post).format(hex);
    }

    private static String toHex(final long value, final int digitNum) {
        StringBuffer result = new StringBuffer(digitNum);

        for (int j = 0; j < digitNum; j++) {
            int index = (int) ((value >> m_shifts[j + (16 - digitNum)]) & 15);
            result.append(m_hexCodes[index]);
        }

        return result.toString();
    }

    public static String toHex(final byte value) {
        return toHex(value, 2);
    }

    public static String toHex(final short value) {
        return toHex(value, 4);
    }

    public static String toHex(final int value) {
        return toHex(value, 8);
    }

    public static String toHex(final long value) {
        return toHex(value, 16);
    }

    public static String toHex(final byte[] value) {
        return toHex(value, 0, value.length);
    }

    public static String toHex(final byte[] value, final int offset,
        final int length) {
        StringBuffer retVal = new StringBuffer();

        for (int x = offset; x < length; x++)
            retVal.append(toHex(value[x]));

        return retVal.toString();
    }

    public static byte[] restoreBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            int c1 = charToNumber(hex.charAt(2 * i));
            int c2 = charToNumber(hex.charAt(2 * i + 1));
            if (c1 == -1 || c2 == -1) {
                return null;
            }
            bytes[i] = (byte) ((c1 << 4) + c2);
        }

        return bytes;
    }

    private static int charToNumber(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xa;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xA;
        } else {
            return -1;
        }
    }

    public static void main(String args[]) {
        String str = "8f230bb011025994594a1a4d8cb3110e79d6d5b479ef654f382144ef23a5fdd58e07bb99911ab16f96e6691bf839102b6f8ecb0540a2e35a2ccd8f6d93810cd64b44da8d458bbe6229235f2c5c0ac2f12a64896ba62523558d022d241a4b5a1a7665c9b432ff0bd89839dfc39f9a5babb32efbf86a22874c6a04031e96427a9b";
        byte[] ba = restoreBytes(str);
        System.out.println(str);

        System.out.println(toHex(ba));
    }
}
