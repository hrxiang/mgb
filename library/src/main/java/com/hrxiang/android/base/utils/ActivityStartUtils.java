package com.hrxiang.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

/**
 * Created by xianghairui on 2018/2/18.
 */

public class ActivityStartUtils {

    public interface IExtras {
        void with(Intent intent);
    }

    public static void start(Context context, Class<?> targetActivity) {
        start(context, targetActivity, null);
    }

    public static void start(Context context, Class<?> targetActivity, IExtras iExtras) {
        Intent intent = new Intent(context, targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        context.startActivity(intent);
    }

    public static void startForResult(Activity sourceActivity, Class<?> targetActivity, int requestCode) {
        startForResult(sourceActivity, targetActivity, requestCode, null);
    }

    public static void startForResult(Fragment sourceFragment, Class<?> targetActivity, int requestCode) {
        startForResult(sourceFragment, targetActivity, requestCode, null);
    }

    public static void startForResult(Activity sourceActivity, Class<?> targetActivity, int requestCode, IExtras iExtras) {
        Intent intent = new Intent(sourceActivity, targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceActivity.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Fragment sourceFragment, Class<?> targetActivity, int requestCode, IExtras iExtras) {
        Intent intent = new Intent(sourceFragment.getActivity(), targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceFragment.startActivityForResult(intent, requestCode);
    }

    public static void setResult(Activity sourceActivity, int resultCode) {
        setResult(sourceActivity, resultCode, null);
    }

    public static void setResult(Activity sourceActivity, int resultCode, IExtras iExtras) {
        Intent intent = new Intent();
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceActivity.setResult(resultCode, intent);
    }
}
