package com.hrxiang.android.net.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by xianghairui on 2018/12/12.
 */
public final class StringUtils {

    /**
     * 保留N位小数
     *
     * @param places 小数点位数
     */
    public static String keeXDecimalPlaces(BigDecimal decimal, int places) {
        StringBuilder pattern = new StringBuilder("#");
        for (int i = 0; i < places; i++) {
            pattern.append("0");
        }
        if (pattern.length() > "#".length()) {
            pattern.insert(1, ".");
        }
        return new DecimalFormat(pattern.toString()).format(decimal);
    }

    /**
     * 百分比, 保留两位小数
     *
     * @param percentSign 是否带百分号
     * @return 5.25%
     */
    public static String percentage(long totalLength, long currentLength, boolean percentSign) {
        if (!percentSign) {
            Double percent = (100.0 * currentLength) / totalLength;
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(percent);
        }
        String percent;
        Double result;
        if (totalLength == 0L) {
            result = 0.0;
        } else {
            result = currentLength * 1.0 / totalLength;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);//控制保留小数点后几位，2：表示保留2位小数点
        percent = nf.format(result);
        return percent;
    }

    /**
     * <p>Compares two Strings, returning <code>true</code> if they are equal.</p>
     * <p>
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     * <p>
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     * both <code>null</code>
     * @see String#equals(Object)
     */
    public static boolean equals(String str1, String str2) {
        return null == str1 ? null == str2 : str1.equals(str2);
    }

    /**
     * <p>Checks if a String is whitespace(" "), empty ("") or null.</p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        return null == str || str.trim().length() == 0;
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     * <p>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p>
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return null == str || str.length() == 0;
    }
}
