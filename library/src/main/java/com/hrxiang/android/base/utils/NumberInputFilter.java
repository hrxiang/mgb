package com.hrxiang.android.base.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by xianghairui on 2018/5/12.
 * 数量输入过滤
 */

public class NumberInputFilter implements InputFilter {
    private int integerLength = 8;
    private int fractionLength = 6;

    public NumberInputFilter() {
    }

    /**
     * @param integerLength  整数长度 默认8位
     * @param fractionLength 小数长度 默认6位
     */
    public NumberInputFilter(int integerLength, int fractionLength) {
        this.integerLength = integerLength;
        this.fractionLength = fractionLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
            int dend) {
        return checking(source.toString(), start, end, dest, dstart, dend);
    }

    private String checking(String source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder buffer = new StringBuilder(dest);
        if (!"".equals(source)) {
            buffer.insert(dstart, source);
        } else {
            buffer.delete(dstart, dstart + 1);
        }
        String result = buffer.toString();
        if (result.startsWith("0") && result.length() != 1 && !result.startsWith("0.")
                || result.startsWith(".")) {
            return "";
        }
        if (dest.toString().contains(".") && !result.contains(".")
                && result.length() > integerLength) {
            return ".";
        }
        String[] values = result.split("\\.");
        if (values[0].length() > integerLength) {
            return "";
        }
        if (values.length > 1 && values[1].length() > fractionLength) {
            return "";
        }
        return null;
    }
}
