package com.hrxiang.android.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import com.hrxiang.android.base.utils.EventBus.EventCenter;

import java.util.Locale;

/**
 * Created by xianghairui on 2018/12/14.
 */
public class LanguageHelper {
    private static final String APP_LANGUAGE = "appLanguage";
    private static final String LANGUAGE_NAME = "languageName";
    public static final int MODIFY_APP_LANGUAGE = 1008668001;
    public static final String FOLLOW_SYSTEM = "followSystem"; //跟随系统
    public static final String SIMPLIFIED_CHINESE = "simplifiedChinese"; //简体中文
    public static final String TRADITIONAL_CHINESE = "traditionalChinese";//繁体中文
    public static final String ENGLISH = "english";  //英语
    public static final String THAILAND = "Thailand";//泰国
    public static final String INDONESIA = "Indonesia";//印尼

    private static void setting(Locale locale) {
        Resources resources = ContextProvider.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, metrics);
        SpHelper.putObj(APP_LANGUAGE, locale);
        EventCenter.post(MODIFY_APP_LANGUAGE);
    }

    private static Locale getSystemLanguage() {
        return Resources.getSystem().getConfiguration().locale;
    }

    public static void followSystem() {
        languageSetting(getSystemLanguage(), FOLLOW_SYSTEM);
    }

    public static void simplifiedChinese() {
        languageSetting(Locale.SIMPLIFIED_CHINESE, SIMPLIFIED_CHINESE);//simplifiedChinese
    }

    public static void traditionalChinese() {
        languageSetting(Locale.TRADITIONAL_CHINESE, TRADITIONAL_CHINESE);//traditionalChinese
    }

    public static void english() {
        languageSetting(Locale.ENGLISH, ENGLISH);//english
    }

    public static void thailand() {
        languageSetting(new Locale("th"), THAILAND);
    }

    public static void indonesia() {
        languageSetting(new Locale("id"), INDONESIA);
    }

    /**
     * 更改app内语言
     */
    private static void languageSetting(Locale locale, String name) {
        SpHelper.putObj(LANGUAGE_NAME, name);
        Locale cache = SpHelper.getObj(APP_LANGUAGE, Locale.class, getSystemLanguage());
        if (!cache.equals(locale)) {
            setting(locale);
        }
    }

    public static void languageSetting(Locale locale) {
        languageSetting(locale, null);
    }

    public static void initAppLanguage() {
        initSystemLanguage();
        if (!getSystemLanguage().equals(getAppCurLanguageSetting())) {
            setting(getAppCurLanguageSetting());
        }
    }

    public static Locale getAppCurLanguageSetting() {
        return SpHelper.getObj(APP_LANGUAGE, Locale.class, getSystemLanguage());
    }

    private static void initSystemLanguage() {
        if (FOLLOW_SYSTEM.equals(getName())) {
            Locale appLanguage = getAppCurLanguageSetting();
            if (!getSystemLanguage().equals(appLanguage)) {
                setting(getSystemLanguage());
            }
        }
    }

    public static String getName() {
        return SpHelper.getObj(LANGUAGE_NAME, String.class, FOLLOW_SYSTEM);
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 8.0需要使用createConfigurationContext处理
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getAppCurLanguageSetting();// getSetLocale方法是获取新设置的语言
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * 跟随系统
     */
    public static String getLanguageFollowSystem() {
        Locale locale = getSystemLanguage();
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("hk".equals(country) || "tw".equals(country)) {
                language = TRADITIONAL_CHINESE;
            } else {
                language = SIMPLIFIED_CHINESE;
            }
        } else if ("th".equals(language)) {
            language = THAILAND;
        } else if ("id".equals(language)) {
            language = INDONESIA;
        } else {
            language = ENGLISH;
        }
        return language;
    }
}
