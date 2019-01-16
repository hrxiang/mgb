package com.hrxiang.android.base.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by xianghairui on 2018/12/25.
 */
public final class ContextProvider {
    private static Application application;

    public static Application getApp() {
        return application;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    /**
     * class DemoApp extends Application{
     *     protected void attachBaseContext(Context base) {
     *        super.attachBaseContext(base);
     *        ContextProvider.attachApp(this);
     *     }
     * }
     **/
    public static void attachApp(Application app) {
        application = app;
    }
}
