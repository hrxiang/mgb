package com.hrxiang.android.net.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by xianghairui on 2019/1/16.
 */
public class GsonFactory {

    public static Gson create() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}
