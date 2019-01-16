package com.hrxiang.android.base.utils;

import android.os.StrictMode;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by hairui.xiang on 2017/8/1.
 */

public class LeakCanaryUtils {
    private static RefWatcher mRefWater;

    private LeakCanaryUtils() {
    }

    public static void initLeakCanary(boolean isOpenLeakCanary) {
        if (isOpenLeakCanary) {
            enabledStrictMode();
            if (!LeakCanary.isInAnalyzerProcess(ContextProvider.getContext())) {
                mRefWater = LeakCanary.install(ContextProvider.getApp());
            }
        }
    }

    public static RefWatcher getRefWatcher() {
        return mRefWater;
    }

    private static void enabledStrictMode() {
        //耗时操作
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        //内存泄漏
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }
}
