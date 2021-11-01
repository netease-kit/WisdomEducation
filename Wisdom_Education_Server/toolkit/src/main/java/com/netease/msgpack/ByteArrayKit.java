package com.netease.msgpack;

public class ByteArrayKit {
    public static final byte[] EMPTY = new byte[0];

    public static boolean isEmpty(byte[] source) {
        return source == null || source.length == 0;
    }
}
