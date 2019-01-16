package com.hrxiang.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by xianghairui on 2018/2/18.
 */

public class ActivityStartUtils {

    public interface IExtras {
        void with(Intent intent);
    }

    public static void jump(Context context, Class<?> targetActivity) {
        jump(context, targetActivity, null);
    }

    public static void jump(Context context, Class<?> targetActivity, IExtras iExtras) {
        Intent intent = new Intent(context, targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        context.startActivity(intent);
    }

    public static void jumpForResult(Activity sourceActivity, Class<?> targetActivity, int requestCode) {
        jumpForResult(sourceActivity, targetActivity, requestCode, null);
    }

    public static void jumpForResult(Fragment sourceFragment, Class<?> targetActivity, int requestCode) {
        jumpForResult(sourceFragment, targetActivity, requestCode, null);
    }

    public static void jumpForResult(Activity sourceActivity, Class<?> targetActivity, int requestCode, IExtras iExtras) {
        Intent intent = new Intent(sourceActivity, targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceActivity.startActivityForResult(intent, requestCode);
    }

    public static void jumpForResult(Fragment sourceFragment, Class<?> targetActivity, int requestCode, IExtras iExtras) {
        Intent intent = new Intent(sourceFragment.getActivity(), targetActivity);
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceFragment.startActivityForResult(intent, requestCode);
    }

    public static void result(Activity sourceActivity, int resultCode) {
        result(sourceActivity, resultCode, null);
    }

    public static void result(Activity sourceActivity, int resultCode, IExtras iExtras) {
        Intent intent = new Intent();
        if (null != iExtras) {
            iExtras.with(intent);
        }
        sourceActivity.setResult(resultCode, intent);
    }
}
