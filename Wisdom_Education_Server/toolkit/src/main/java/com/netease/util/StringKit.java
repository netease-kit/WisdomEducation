package com.netease.util;

import java.nio.charset.StandardCharsets;

public class StringKit {

    /**
     * A String for null.
     */
    public static final String NULL = "null";

    /**
     * A String for a space character.
     */
    public static final String SPACE = " ";

    /**
     * The empty String {@code ""}.
     */
    public static final String EMPTY = "";

    /**
     * A String for linefeed LF ("\n").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     */
    public static final String LF = "\n";

    /**
     * A String for carriage return CR ("\r").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     */
    public static final String CR = "\r";

    /**
     * Represents a failed index search.
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * serialize
     *
     * @param string
     * @return
     */
    public static byte[] serialize(String string) {
        return (string == null ? null : string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * deserialize
     *
     * @param bytes
     * @return
     */
    public static String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, StandardCharsets.UTF_8));
    }

    public static String trim(String str){
        return str == null ? null : str.trim();
    }

}
