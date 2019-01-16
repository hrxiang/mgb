package com.hrxiang.android.base.utils;

import android.os.Environment;

import java.io.File;


/**
 * Created by hairui.xiang on 2017/8/22.
 * "/Android/data/" + context.getPackageName() + "/cache/"
 */

public class StorageCardUtils {
    public static final String IMAGE_DIR = "images";//图片存储目录
    public static final String DOWNLOAD_DIR = "downloads";//apk版本更新存放目录
    public static final String HTTP_DIR = "https";//http请求
    public static final String H5_ASSET_PREFIX = "file:///android_asset/";
    public static final String H5_SDCARD_PREFIX = "content://com.android.htmlfileprovider/";

    public static File getAppCacheDir() {
        File dir = isExistSDCard() ? getExternalCacheDir() : getInnerCacheDir();
        if (null != dir && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getAppFilesDir() {
        File dir = isExistSDCard() ? getExternalFilesDir() : getInnerFilesDir();
        if (null != dir && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * sd card
     */
    public static File getExternalCacheDir() {
        return ContextProvider.getContext().getExternalCacheDir();
        //return new File(Environment.getExternalStorageDirectory(), "/Android/data/" + BaseApp.getInstance().getPackageName() + "/cache/");
    }

    /**
     * RAM
     */
    public static File getInnerCacheDir() {
        return ContextProvider.getContext().getCacheDir();
    }

    public static File getExternalFilesDir() {
        return ContextProvider.getContext().getExternalFilesDir(null);
        //return new File(Environment.getExternalStorageDirectory(), "/Android/data/" + BaseApp.getInstance().getPackageName() + "/files/");
    }

    public static File getInnerFilesDir() {
        return ContextProvider.getContext().getFilesDir();
    }


    public static boolean isExistSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
