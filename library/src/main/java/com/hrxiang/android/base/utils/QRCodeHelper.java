package com.hrxiang.android.base.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hairui.xiang on 2017/9/8.
 */
public class QRCodeHelper {
    public static final int RC_GET_QRCODE = 4004;//获取二维码

    public interface OnQRCodeGetListener {
        void onGetQRCode(String result);
    }

    public static void openQRCodeScanner(Activity activity, Class<?> cls) {
        ActivityStartUtils.startForResult(activity, cls, RC_GET_QRCODE);
    }

    public static void openQRCodeScanner(Fragment fragment, Class<?> cls) {
        ActivityStartUtils.startForResult(fragment, cls, RC_GET_QRCODE);
    }

    public static void openQRCodeScanner(Activity activity, Class<?> cls, ActivityStartUtils.IExtras iExtras) {
        ActivityStartUtils.startForResult(activity, cls, RC_GET_QRCODE, iExtras);
    }

    public static void openQRCodeScanner(Fragment fragment, Class<?> cls, ActivityStartUtils.IExtras iExtras) {
        ActivityStartUtils.startForResult(fragment, cls, RC_GET_QRCODE, iExtras);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data, OnQRCodeGetListener l) {
        if (RESULT_OK == resultCode && null != data) {
            switch (requestCode) {
                case RC_GET_QRCODE:
                    if (null != l) {
                        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
                        String content = result.getContents();
                        if (null != content) {
                            l.onGetQRCode(content);
                        }
                    }
                    break;
            }
        }
    }
}
