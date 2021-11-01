package com.netease.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

public class RedisUtil {

    private static final Joiner JOINER = Joiner.on(":").useForNull(StringKit.NULL);

    private static final Splitter SPLITTER = Splitter.on(":").trimResults();

    public static String joinKey(Object... parts) {
        if(parts.length == 0){
            return "";
        }
        if(parts.length == 1){
            return String.valueOf(parts[0]);
        }
        return JOINER.join(parts);
    }

    public static List<String> splitKey(String key) {
        return SPLITTER.splitToList(key);
    }
}
