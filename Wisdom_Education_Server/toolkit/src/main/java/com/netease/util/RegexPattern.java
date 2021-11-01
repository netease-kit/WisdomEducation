package com.netease.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum  RegexPattern {
    DOMESTIC_MOBILE("^(0086-|0086|\\+86|\\+86-)?0?(1[3-9][\\d-]{9})$"),
    FOREIGN_MOBILE("^(00|\\+)([1-9]\\d*?(?<!0086|\\+86)-)(\\d+)$"),
    QR_META("qr/(?:p|t)/([^/?]+)(?:\\?appUrl=(.+))?"),
    PHONE_NUMBER("^(00|\\+)(86)?-?0?(\\d+)$");
    private Pattern pattern;

    RegexPattern(String string) {
        this.pattern = Pattern.compile(string);
    }

    public boolean matches(String string){
        return pattern.matcher(string).matches();
    }

    public List<String> groups(int index, String string){
        Matcher m = pattern.matcher(string);
        List<String> groups = Lists.newArrayList();
        if (m.find()){
            for (int i = index; i < m.groupCount(); i++) {
                groups.add(m.group(i+1));
            }
        }
        return groups;
    }

    public String group(int index, String string){
        Matcher m = pattern.matcher(string);
        if(m.find()){
            return m.group(index);
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(DOMESTIC_MOBILE.groups("+86-18667655545"));
//        System.out.println(DOMESTIC_MOBILE.groups("008618667655545"));
//        System.out.println(DOMESTIC_MOBILE.groups("+86-018667655545"));
//        System.out.println(DOMESTIC_MOBILE.groups("0086018667655545"));
//        System.out.println(DOMESTIC_MOBILE.groups("018667655545"));
//        System.out.println(DOMESTIC_MOBILE.groups("18667655545"));
//
//        System.out.println(FOREIGN_MOBILE.groups("008601234567"));
//        System.out.println(FOREIGN_MOBILE.groups("00886-1234567"));
//        System.out.println(FOREIGN_MOBILE.groups("00886-01234567"));
//        System.out.println(FOREIGN_MOBILE.groups("001-01234567"));
//        System.out.println(FOREIGN_MOBILE.groups("001-1234567"));

//        System.out.println(PhoneNumber.format("+86-18667655545"));
//        System.out.println(PhoneNumber.format("008618667655545"));
//        System.out.println(PhoneNumber.format("+86-018667655545"));
//        System.out.println(PhoneNumber.format("+86018667655545"));
//        System.out.println(PhoneNumber.format("+8618667655545"));
//        System.out.println(PhoneNumber.format("0086018667655545"));
//        System.out.println(PhoneNumber.format("018667655545"));
//        System.out.println(PhoneNumber.format("018667655545"));
//
//        System.out.println(PhoneNumber.format("00886-1234567"));
//        System.out.println(PhoneNumber.format("00886-01234567"));
//        System.out.println(PhoneNumber.format("001-01234567"));
//        System.out.println(PhoneNumber.format("001-1234567"));
//        System.out.println(PhoneNumber.format("+1-1234567"));
//        System.out.println(PhoneNumber.format("+886-1234567"));

        System.out.println(PhoneNumber.format("+86-18768139111"));
    }
}
