package com.hrxiang.android.base.utils;

import java.util.regex.Pattern;

/**
 * Created by xianghairui on 2019/3/7.
 */
public class RegexUtils {
    /**
     * 匹配姓名,只能是中文或英文，不能为数字或其他字符，汉字和字母不能同时出现 
     */
    public static final String NAME_REGEXP = "(([\u4E00-\u9FA5]{2,7})|([a-zA-Z]{3,10}))";
    /**
     * 正则表达式:验证密码(含特殊字符)
     */
    public static final String REGEX_PASSWORD_1 = "^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{8,}$";
    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD_2 = "^[a-zA-Z0-9]{1,12}$";

    /**
     * 正则表达式:验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 正则表达式:验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式:验证汉字(1-9个汉字)  {1,9} 自定义区间
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]{1,9}$";

    /**
     * 正则表达式:验证身份证
     */
    public static final String REGEX_ID_CARD = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";

    /**
     * 正则表达式:验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式:验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";

    /**
     * 匹配邮编代码 
     */
    public static final String ZIP_CODE_REGEXP = "^[0-9]{6}$";// 匹配邮编代码  

    /**
     * 匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static final String LETTER_NUMBER_UNDERLINE_REGEXP = "^//w+$";

    /**
     * @param regex 正则表达式
     * @param input 被验证的字符串
     * @return true符合规则，false不符合规则
     */
    public static boolean isMatches(String regex, CharSequence input) {
        return Pattern.matches(regex, input);
    }
}
