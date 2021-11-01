package com.netease.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtil {

    public static final DateTimeFormatter DATE = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static final DateTimeFormatter BASIC_DATE = DateTimeFormat.forPattern("yyyyMMdd");

    public static final DateTimeFormatter DATE_TIME = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter BASIC_DATE_TIME = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    public static long getMillis() {
        return System.currentTimeMillis();
    }

    public static int getTime() {
        return (int) (getMillis() / 1000);
    }

    public static Date tomorrowStart(){
       DateTime dateTime = new DateTime(Now.getMillis());

       return dateTime.withTime(0,0,0,0).plusDays(1).toDate();
    }

    public static Date hourlyStart(){
        DateTime dateTime = new DateTime(Now.getMillis());
        return dateTime.withTime(dateTime.getHourOfDay(),0,0,0).plusHours(1).toDate();
    }

    public static void main(String[] args) {
        System.out.println(tomorrowStart());
    }
}
