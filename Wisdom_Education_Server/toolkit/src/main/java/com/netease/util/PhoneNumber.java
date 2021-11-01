package com.netease.util;

import com.google.common.base.Joiner;

import java.util.List;

public class PhoneNumber {
    private String prefix;
    private String suffix;

    public PhoneNumber(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public static String format(String string){
        if(!Conf.getBoolean("MobileFormatCheckEnabled", false)){
            return string;
        }
        string = string.trim();
        List<String> groups = RegexPattern.DOMESTIC_MOBILE.groups(0, string);
        if(groups.size() == 0){
            groups = RegexPattern.FOREIGN_MOBILE.groups(0, string);
            if(groups.size() == 0){
                return null;
            }
            if(groups.get(0).equals("00")){
                groups.set(0, "+");
            }
            return Joiner.on("").join(groups);
        }else {
            return groups.get(groups.size() - 1);
        }
    }
}
