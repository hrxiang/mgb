package com.hrxiang.android.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;


public class SpHelper {
    private static final String SP_NAME = "sp_cache";

    private static Context getContext() {
        return ContextProvider.getContext();
    }

    public static SharedPreferences getSharedPreferences() {
        return getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private static String getIdentity(String identity, String key) {
        return identity == null ? key : identity + "_" + key;
    }

    public static <T> void putObj(String identity, String key, T obj) {
        putObj(getIdentity(identity, key), obj);
    }

    public static <T> void putObj(String key, T obj) {
        getEditor().putString(key, JsonUtils.toString(obj)).apply();
    }

    public static <T> void putList(String identity, String key, List<T> lst) {
        putList(getIdentity(identity, key), lst);
    }

    public static <T> void putList(String key, List<T> lst) {
        getEditor().putString(key, JsonUtils.toString(lst)).apply();
    }

    public static <T> T getObj(String identity, String key, Class<T> clazz, T defaultVale) {
        return getObj(getIdentity(identity, key), clazz, defaultVale);
    }

    public static <T> T getObj(String key, Class<T> clazz, T defaultVale) {
        T t = defaultVale;
        try {
            String jsonStr = getSharedPreferences().getString(key, null);
            if (null != jsonStr) {
                t = JsonUtils.toObject(jsonStr, clazz);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T getObj(String identity, String key, Class<T> clazz) {
        return getObj(getIdentity(identity, key), clazz, null);
    }

    public static <T> T getObj(String key, Class<T> clazz) {
        return getObj(key, clazz, null);
    }

    public static <T> List<T> getList(String identity, String key, Class<T> clazz) {
        return getList(getIdentity(identity, key), clazz);
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        return JsonUtils.toList(getSharedPreferences().getString(key, null), clazz);
    }

    public static boolean remove(String identity, String key) {
        return remove(getIdentity(identity, key));
    }

    public static boolean remove(String key) {
        return getSharedPreferences().edit().remove(key).commit();
    }

    public static boolean clear() {
        return getSharedPreferences().edit().clear().commit();
    }
}
