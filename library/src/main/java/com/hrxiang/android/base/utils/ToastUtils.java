package com.hrxiang.android.base.utils;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ToastUtils {
    private static volatile ToastUtils mUtils;
    private static Toast mToast;
    private View mView;
    private int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    private int xOffset = 0;
    private int yOffset = (int) (64 * getContext().getResources().getDisplayMetrics().density + 0.5);

    public static ToastUtils getInstance() {
        if (null == mUtils) {
            synchronized (ToastUtils.class) {
                if (null == mUtils) {
                    mUtils = new ToastUtils();
                }
            }
        }
        return mUtils;
    }

    public ToastUtils setView(View view) {
        this.mView = view;
        return this;
    }

    public ToastUtils setView(@LayoutRes int layoutId) {
        this.mView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        return this;
    }

    public View getView() {
        return null == mToast ? mView : mToast.getView();
    }

    public ToastUtils setGravity(int gravity) {
        this.mGravity = gravity;
        return this;
    }

    public ToastUtils setXOffset(int xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    public ToastUtils setYOffset(int yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    public void showShort(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public void showShort(@StringRes int resId) {
        show(getContext().getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public void shwLong(@StringRes int resId) {
        show(getContext().getResources().getText(resId), Toast.LENGTH_LONG);
    }

    private void show(CharSequence text, int duration) {
        cancel();
        if (null != mView) {
            mToast = new Toast(getContext());
            mToast.setView(mView);
            mToast.setDuration(duration);
            mToast.setGravity(mGravity, xOffset, yOffset);
            mToast.show();
        } else {
            mToast = Toast.makeText(getContext(), text, duration);
            mToast.setGravity(mGravity, xOffset, yOffset);
            mToast.show();
        }
    }

    public void cancel() {
        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }
    }

    private static Context getContext() {
        return ContextProvider.getContext();
    }
}
