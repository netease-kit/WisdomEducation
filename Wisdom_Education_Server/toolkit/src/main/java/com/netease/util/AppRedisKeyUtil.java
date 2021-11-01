package com.netease.util;

public class AppRedisKeyUtil {

    public static String REDIS_TYPE_MISC = "misc";

    public static final String misckey(String... fields) {
        return key(REDIS_TYPE_MISC, fields);
    }

    public static final String key(String type, String... fields) {
        return type + "|" + String.join("|", fields);
    }

    public static void main(String[] args) {
        System.out.println(key(REDIS_TYPE_MISC, "test", "2019-01-01", "999"));
    }
}
