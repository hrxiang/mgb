package com.hrxiang.android.app;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hrxiang.android.app.utils.HttpLogger;
import com.hrxiang.android.base.BaseApplication;
import com.hrxiang.android.base.utils.AppUtils;
import com.hrxiang.android.base.utils.LeakCanaryUtils;
import com.hrxiang.android.base.utils.StorageCardUtils;
import com.hrxiang.android.net.core.HttpFactory;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;

/**
 * Created by xianghairui on 2019/1/17.
 */
public class DemoApp extends BaseApplication {
    @Override
    protected void onCreate1() {
        super.onCreate1();
        if (AppUtils.isMainProcess()) {
            initNet();
        }
    }

    @Override
    protected void initLogger() {
        super.initLogger();
        XLog.init(LogLevel.ALL);
    }

    @Override
    protected void initLeakCanary() {
        super.initLeakCanary();
        LeakCanaryUtils.initLeakCanary(isDebug());
    }

    void initNet() {
        new HttpFactory.Builder()
                .baseUrl("https://www.apiopen.top/")
                .readTimeout(30)
                .writeTimeout(30)
                .connectTimeout(30)
                .addInterceptors(new HttpLoggingInterceptor(new HttpLogger()).setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptors(new StethoInterceptor())
                .cacheDir(getHttpCacheDir())
                .appVersion(AppUtils.getVersionCode())
                .maxSize(1024 * 1024 * 100)
                .build();
    }

    private File getHttpCacheDir() {
        return new File(StorageCardUtils.getAppCacheDir(), StorageCardUtils.HTTP_DIR);
    }
}
