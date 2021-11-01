package com.netease.util;

public class MaskUtil {

    public static boolean isOn(long bits, long mask){
        return (bits & mask) != 0;
    }

    public static boolean isOff(long bits, long mask){
        return (bits & mask) == 0;
    }

    public static long setOn(long bits, long mask){
        return bits | mask;
    }

    public static long setOff(long bits, long mask){
        return bits & ~mask;
    }
}
