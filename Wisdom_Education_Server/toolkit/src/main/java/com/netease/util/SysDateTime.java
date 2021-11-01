package com.netease.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SysDateTime {

    private static final String[] pattern = new String[] { "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyyMMdd", "yyyy-MM-dd",
            "yyyy/MM/dd", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss" };
    /**
     * parse date by format "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyyMMdd",
     * "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss",
     * "yyyy/MM/dd HH:mm:ss"<br/>
     * return null if ParseException
     */
    public static Date parseDate(String str) {
        try {
            if (StringUtils.isBlank(str)) {
                return null;
            }
            return DateUtils.parseDate(str, pattern);
        } catch (ParseException e) {
            return null;
        }
    }
    public static String getNowString() {
        return Long.toString((new Date()).getTime() / 1000);
    }

    /**
     * Get current time in second
     *
     * @return
     */
    public static int getNow() {
        return (int) ((new Date()).getTime() / 1000);
    }

    public static String getDatetime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (formatter.format(new Date()));
    }

    public static String getDatetimeNDaysAgo(int ndays) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -ndays);
        return (formatter.format(cal.getTime()));
    }

    public static int getDatetimeNDaysAgoTimetag(int ndays) {
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -ndays);
        System.out.println(cal.getTime());
        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static String getDatetime_millisecond() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.S");
        return (formatter.format(new Date()));
    }

    public static String getDatetime(Date datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(datetime);
    }

    public static String getDatetime(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (formatter.format(new Date(date)));
    }

    public static int getDatetime(String dateStr) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(dateStr);
        return (int) (date.getTime() / 1000);
    }

    public static Date getDate(String sDate) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.parse(sDate);
    }

    public static String getDateOnly(String datetime) throws Exception {
        String[] ss = datetime.split("\\s");
        if (ss.length >= 1) {
            return ss[0];
        }
        return null;
    }

    public static String getDateOnly(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return (formatter.format(new Date(date)));
    }

    public static boolean checkString(String datetime) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            formatter.parse(datetime);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    // 如果格式不是很规范，如6月没有写成06等，转换成规范的格式
    public static String adjust(String s) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(s);
        return formatter.format(new Date(date.getTime()));
    }

    // 取下一个时间，time只包括 HH:mm:ss 部分
    public static Date getNextDateTime(String time) throws Exception {
        String day = SysDateTime.getDateOnly(Now.getDatetime());

        Calendar ca = Calendar.getInstance();
        ca.setTime(SysDateTime.getDate(day + " " + time));

        Calendar ca_now = Calendar.getInstance();

        // 如果设定的时间在现在之前了，则到下一天
        if (ca.before(ca_now)) {
            ca.add(Calendar.DAY_OF_MONTH, 1);
        }

        return ca.getTime();
    }

    // 取下一个时间，以week为周期，如果没过，取本周的，过了取下周的。
    // time只包括 HH:mm:ss 部分
    public static Date getNextDateTimeByWeek(int theday, String time)
            throws Exception {
        // 转换成因为习惯的星期
        int ca_theday = getCalendarWeekday(theday);

        Calendar ca_today = Calendar.getInstance();
        String day = SysDateTime.getDateOnly(Now.getDatetime());
        ca_today.setTime(SysDateTime.getDate(day + " " + time));

        Calendar ca_thisweek = (Calendar) ca_today.clone();
        ca_thisweek.set(Calendar.DAY_OF_WEEK, ca_theday);

        // 用当前的时间与本周计划时间和下周计划时间比较
        Calendar ca_now = Calendar.getInstance();
        if (ca_now.before(ca_thisweek)) {
            return ca_thisweek.getTime();
        } else {
            Calendar ca_nextweek = (Calendar) ca_thisweek.clone();
            ca_nextweek.add(Calendar.DAY_OF_WEEK, 7);
            return ca_nextweek.getTime();
        }
    }

    private static int getCalendarWeekday(int theday) {
        if (theday == 1)
            return Calendar.MONDAY;
        if (theday == 2)
            return Calendar.TUESDAY;
        if (theday == 3)
            return Calendar.WEDNESDAY;
        if (theday == 4)
            return Calendar.THURSDAY;
        if (theday == 5)
            return Calendar.FRIDAY;
        if (theday == 6)
            return Calendar.SATURDAY;
        if (theday == 7)
            return Calendar.SUNDAY;
        return -1;
    }

    public static String getAbsoluteDatetime(Long seconds) {
        long day = seconds / (24 * 3600);
        long hour = seconds % (24 * 3600) / 3600;
        long minute = seconds % 3600 / 60;
        long second = seconds % 60;
        return String.format("%dd, %02d:%02d:%02d", day, hour, minute, second);
    }

    public static String getReadableTimetag(Integer timetag) {
        return (timetag == null) ? "\\N" : (timetag + " ["
                + getDatetime(timetag.longValue() * 1000) + "]");
    }

    // 获取当月第一天
    public static Long getFirstDayOfMonth() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.set(Calendar.AM_PM, Calendar.AM);
        lastDate.set(Calendar.HOUR, 0);
        lastDate.set(Calendar.MINUTE, 0);
        lastDate.set(Calendar.SECOND, 0);
        lastDate.set(Calendar.MILLISECOND, 0);
        return lastDate.getTimeInMillis();
    }

    public static void main(String[] args) throws Exception {
        // int now = Now.getNow();
        // System.out.println(now);
        System.out.println(getFirstDayOfMonth());
        // System.out.println(getDatetimeNDaysAgoTimetag(19));
//       System.out.println(getAbsoluteDatetime(1549466L));
    }
}
