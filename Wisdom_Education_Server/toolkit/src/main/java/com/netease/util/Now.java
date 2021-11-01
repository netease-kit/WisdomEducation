package com.netease.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Now {
    public static String getDatetime() {
        return null;
    }

    public static final DateTimeFormatter DATE = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static final DateTimeFormatter BASIC_DATE = DateTimeFormat.forPattern("yyyyMMdd");

    public static final DateTimeFormatter DATE_TIME = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter BASIC_DATE_TIME = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    public static long getMillis() {
        return System.currentTimeMillis();
    }

    public static long getTimeSeconds() {
        return getMillis() / 1000;
    }

    public static long getTodayMillisWithZone(int offset){
        return new DateTime().withZone(DateTimeZone.forOffsetHours(offset)).withTime(0,0,0,0).toDate().getTime();
    }

    public static void main(String[] args) {
        System.out.println(new DateTime().withZone(DateTimeZone.forOffsetHours(8)).withTime(0,0,0,0).toDate().getTime());
    }

    public static long getMillisAfterDays(int d){
        return System.currentTimeMillis() + d * 86400000L;
    }
}
