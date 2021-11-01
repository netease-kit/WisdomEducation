package com.netease.util;



import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    private static final Pattern versionPattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    private static final Pattern fixedPhonePattern = Pattern
            .compile("^(\\+?0*86-?)?(0+)?([0-9]{2,3})?([2-8][0-9]{6,7})([0-9]{1,4})?$");

    private static final Pattern mobilePattern = Pattern
            .compile("^\\+\\d{1,4}-\\d+|1[3,4,5,7,8]\\d{9}");

    private static final Pattern domesticMobilePattern = Pattern
            .compile("^1[3,4,5,7,8]\\d{9}");

    // example:["18612345678", "186-1234-5678", "186 1234 5678", "186 - 1234 - 5678"]
    private static final Pattern broadenDomesticMobilePattern = Pattern
            .compile("1[3,4,5,7,8](\\s*-?\\s*\\d){9}");

    private static final Pattern domesticMobileIllegalCharsPattern = Pattern.compile("\\s*-?\\s*");

    private static final Pattern internationMobilePattern = Pattern
            .compile("^\\+\\d{1,4}-\\d+");

    private static final Pattern foreignerMobilePattern = Pattern
            .compile("^\\d{5,}$");

    private static final Pattern telecomMobilePattern = Pattern
            .compile("^((133|149|153|173|177|180|181|189)\\d{8})|((1700|1701|1702)\\d{7})");

    private static final Pattern unicomMobilePattern = Pattern
            .compile("^((130|131|132|145|155|156|171|175|176|185|186)\\d{8})|((1707|1708|1709)\\d{7})");

    private static final Pattern emailPattern = Pattern
            .compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private static final Pattern mobileEmailPattern = Pattern
            .compile("^1[3,4,5,7,8]\\d{9}@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+");

    private static final Pattern yidPattern = Pattern
            .compile("[a-zA-Z][a-zA-Z0-9_]{5,19}"); // XX号：以字母开头的字母+数字+下划线，6-20位

    private static final Pattern bidPattern = Pattern.compile("^[0-9a-zA-Z_]{6,20}"); // 联盟号 字母+数字+下划线，6-20位

    private static final Pattern inviteCodePattern = Pattern.compile("^[0-9a-zA-Z]{6,10}"); // 邀请码 字母+数字，6-10位，ios限制了，android没限制

    private static final Pattern yidPatternForFindUser = Pattern
            .compile("[a-zA-Z][a-zA-Z0-9_]{4,19}"); // XX号：以字母开头的字母+数字+下划线，5-20位

    private static final Pattern ecpidPattern = Pattern
            .compile(".+@ecplive\\.com");

    private static final String ipDigitPattern = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";

    private static final Pattern ipPattern = Pattern.compile(String.format(
            "%s(\\.%s){3}", ipDigitPattern, ipDigitPattern));

    private static final Pattern internalIpPattern = Pattern
            .compile(String.format("(10(\\.%s){3})|(172\\.(1[6-9]|2\\d|3[01])(\\.%s){2})|(192\\.168(\\.%s){2})|(127\\.0\\.0\\.%s)",
                    ipDigitPattern, ipDigitPattern, ipDigitPattern, ipDigitPattern));

    private static final Pattern numbersStringPattern = Pattern.compile("\\d+");

    private static final Pattern uuidPattern = Pattern
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static Pattern iphoneOsPattern = Pattern
            .compile("OSVersion: (.+?) Device: (.+)");

    public static Pattern mentionPattern = Pattern.compile("@([\\u4e00-\\u9fa5A-Z0-9a-zéëêèàâäáùüûúìïîí._-]+)");

    public static Pattern nickPattern = Pattern.compile("^[\\u4e00-\\u9fa5A-Z0-9a-zéëêèàâäáùüûúìïîí._-]+$");

    private static final Pattern datePattern = Pattern.compile("^(19|20)\\d{2}-(1[0-2]|0[1-9])-(0[1-9]|[1-2][0-9]|3[0-1])$");

    public static final String yearMonthRegEx = "^(19|20)\\d{2}-(1[0-2]|0[1-9])$";

    private static final Pattern yearMonthPattern = Pattern.compile(yearMonthRegEx);

    public static final Pattern containsLetterPattern = Pattern.compile("^\\w*[a-zA-Z]\\w*$");

    public static boolean isVersion(String str) {
        return versionPattern.matcher(str).matches();
    }

    public static boolean isUUID(String str) {
        return uuidPattern.matcher(str).matches();
    }

    public static boolean isNumbersString(String str) {
        return numbersStringPattern.matcher(str).matches();
    }

    public static boolean isFixedPhone(String str) {
        return fixedPhonePattern.matcher(str).matches();
    }

    public static boolean isMobile(String str) {
        return mobilePattern.matcher(str).matches();
    }

    public static boolean isDomesticMobile(String str) {
        return domesticMobilePattern.matcher(str).matches();
    }

    public static boolean isInternationMobile(String str) {
        return internationMobilePattern.matcher(str).matches();
    }

    public static boolean isForeignerMobile(String str) {
        return foreignerMobilePattern.matcher(str).matches();
    }

    public static boolean isTelecomMobile(String str) {
        return telecomMobilePattern.matcher(str).matches();
    }

    public static boolean isUnicomMobile(String str) {
        return unicomMobilePattern.matcher(str).matches();
    }

    public static boolean isEmail(String str) {
        return emailPattern.matcher(str).matches();
    }

    public static boolean isMobileEmail(String str) {
        return mobileEmailPattern.matcher(str).matches();
    }

    public static boolean isYid(String str) {
        return yidPattern.matcher(str).matches();
    }

    public static boolean isYidForFindUser(String str) {
        return yidPatternForFindUser.matcher(str).matches();
    }

    public static boolean isEcpid(String str) {
        return ecpidPattern.matcher(str).matches();
    }

    public static boolean isInternalIp(String str) {
        return internalIpPattern.matcher(str).matches();
    }

    public static boolean isIp(String str) {
        return ipPattern.matcher(str).matches();
    }

    public static boolean isBid(String str) {
        return bidPattern.matcher(str).matches();
    }

    public static boolean isInviteCode(String str) {
        return inviteCodePattern.matcher(str).matches();
    }

    public static boolean isContainsLetter(String str) {
        return containsLetterPattern.matcher(str).matches();
    }

    public static void main(String[] args) {

    }

    public static boolean isBroadenDomesticMobile(String str) {
        return broadenDomesticMobilePattern.matcher(str).matches();
    }

    public static boolean isIphoneOs(String str) {
        return iphoneOsPattern.matcher(str).matches();
    }

    public static String normalizeDomesticMobile(String str) {
        return domesticMobileIllegalCharsPattern.matcher(str).replaceAll("");
    }

    public static boolean isValidNick(String nick) {
        if (Strings.isNullOrEmpty(nick)) {
            return false;
        }
        return nickPattern.matcher(nick).find();
    }

    public static final String validEndDateEx = "^now$|^(19|20)\\d{2}-(1[0-2]|0[1-9])$";

    public static boolean isValidDate(String str) {
        return datePattern.matcher(str).matches();
    }

    public static boolean isValidYearMonth(String str) {
        return yearMonthPattern.matcher(str).matches();
    }

    public static Set<String> getMentionNicks(String text) {
        Matcher m = mentionPattern.matcher(text);
        Set<String> list = Sets.newHashSet();
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }
}
