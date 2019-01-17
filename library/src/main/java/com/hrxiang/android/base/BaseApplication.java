package com.hrxiang.android.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hrxiang.android.base.utils.AppUtils;
import com.hrxiang.android.base.utils.ContextProvider;
import com.hrxiang.android.base.utils.LanguageHelper;
import com.hrxiang.android.base.utils.fresco.ImageConfig;

/**
 * Created by xianghairui on 2018/12/14.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        onCreate1();
    }

    protected void onCreate1() {
        initAppLanguage();
        initLogger();
        initFresco();
        initLeakCanary();
    }

    protected void initAppLanguage() {
        LanguageHelper.initAppLanguage();
    }

    /**
     * 初始化fresco
     */
    protected void initFresco() {
        Fresco.initialize(this, ImageConfig.getOkHttpImagePipelineConfig(this));
    }

    /**
     * 内存泄漏以及anr监控
     */
    protected void initLeakCanary() {
//        LeakCanaryUtils.initLeakCanary(isDebug());
    }

    /**
     * 打印日志
     */
    protected void initLogger() {
    }

    /**
     * default true
     */
    protected boolean isDebug() {
        return AppUtils.isDebug();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ImageConfig.trimMemory();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageConfig.clearMemoryCaches();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged1(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public void onConfigurationChanged1(Configuration newConfig) {
        notFollowSystemFrontSizeChange(newConfig);
        initAppLanguage();
    }

    /**
     * 8.0以上app内 语言切换失效bug
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        attachBaseContext1(base);
    }

    protected void attachBaseContext1(Context base) {
        MultiDex.install(base);
        ContextProvider.attachApp(this);
        LanguageHelper.attachBaseContext(base);
    }

    /**
     * 字体不跟随系统改变
     *
     * @param newConfig 系统信息
     */
    private void notFollowSystemFrontSizeChange(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            Resources res = super.getResources();
            if (res.getConfiguration().fontScale != 1) {
                Configuration config = new Configuration();
                config.setToDefaults();
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
    }
}

