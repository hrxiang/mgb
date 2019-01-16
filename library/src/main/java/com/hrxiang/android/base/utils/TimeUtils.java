package com.hrxiang.android.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hairui.xiang on 2017/8/24.
 */

public class TimeUtils {

    /**
     * 将时间转换为时间戳 精确到毫秒
     */
    public static long timestamp(String timeStr, String pattern) throws ParseException {
        return createFormat(pattern).parse(timeStr).getTime();
    }

    /**
     * 时间戳 精确到毫秒
     */
    public static long timestamp() {
        return new Date().getTime();
    }

    /**
     * 将时间戳转换为时间
     */
    public static Date parse(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 格式化时间
     *
     * @param timeStr  时间字符串
     * @param sPattern 源格式
     * @param dPattern 目标格式
     * @return 目标格式的时间字符串
     */
    public static String format(String timeStr, String sPattern, String dPattern)
            throws ParseException {
        return createFormat(dPattern).format(parse(timeStr, sPattern));
    }

    public static String format(long timestamp, String dPattern) {
        return createFormat(dPattern).format(new Date(timestamp));
    }

    public static String format(String pattern) {
        return createFormat(pattern).format(new Date());
    }

    /**
     * 解析时间
     *
     * @param timeStr 时间字符串
     * @param pattern 时间的格式
     * @return Date
     **/
    public static Date parse(String timeStr, String pattern) throws ParseException {
        return createFormat(pattern).parse(timeStr);
    }

    private static SimpleDateFormat createFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA);
    }

    /**
     * 获取过去第几天的日期
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        return createFormat("yyyy-MM-dd").format(today);
    }
}
