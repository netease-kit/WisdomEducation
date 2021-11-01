package com.netease.util;

import com.google.common.collect.Maps;

import java.text.DecimalFormat;
import java.util.Map;

public class NumberUtil {
    private static Map<Integer, DecimalFormat> map = Maps.newHashMap();
    static{
        map.put(4, new DecimalFormat("0000"));
        map.put(5, new DecimalFormat("00000"));
        map.put(6, new DecimalFormat("000000"));
    }
    public static String random(int len){
        if(len > 6 || len < 4){
            return null;
        }
        return map.get(len).format((int)(Math.random() * (Math.pow(10, len))));
    }
    public static String humanReadable(int i) {
        double r = i;
        String unit;
        if (r > 1024) {
            r = i / 1024d;
            if (r > 1024) {
                r = r / 1024d;
                if (r > 1024) {
                    r = r / 1024d;
                    unit = "GB";
                } else {
                    unit = "MB";
                }
            } else {
                unit = "KB";
            }
        } else {
            unit = "B";
        }
        return new DecimalFormat("0.00").format(r)+ unit;
    }
}
