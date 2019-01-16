package com.hrxiang.android.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.*;
import com.hrxiang.android.base.R;


/**
 * Created by hairui.xiang on 2017/9/18.
 */

public class DialogHelper {

    private DialogHelper() {
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    final public static class Builder {
        private View mView;
        private Context mContext;
        private Dialog mDialog;
        private LayoutInflater mInflater;
        private float mRatio;
        private int mOffsetY;
        private boolean mBackgroundDimEnabled = true;
        private int mGravity = Gravity.CENTER;
        private boolean isWidthFullScreen;
        private int themeResId = -1;
        private int layoutResID = -1;
        private float dimAmount = 0.5f;

        public Builder(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        public Builder setThemeResId(int themeResId) {
            this.themeResId = themeResId;
            return this;
        }

        public Builder setWidthFullScreen(boolean widthFullScreen) {
            isWidthFullScreen = widthFullScreen;
            return this;
        }

        public Builder setContentView(View view) {
            this.mView = view;
            return this;
        }

        public Builder setContentView(int layoutResID) {
            this.layoutResID = layoutResID;
            return this;
        }

        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder setOffsetY(int mOffsetY) {
            this.mOffsetY = mOffsetY;
            return this;
        }

        public Builder setWidthRatio(float ratio) {
            this.mRatio = ratio;
            return this;
        }

        public Builder setBackgroundDimEnabled(boolean Enabled) {
            this.mBackgroundDimEnabled = Enabled;
            return this;
        }

        private int getScreenWidth() {
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            return dm.widthPixels;
        }

        public Builder setGravity(int mGravity) {
            this.mGravity = mGravity;
            return this;
        }

        private void windowSetting() {
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            if (isWidthFullScreen) {
                View decorView = window.getDecorView();
                if (null != decorView) {
                    decorView.setPadding(0, 0, 0, 0);
                }
            } else {
                if (0 != mRatio) {
                    int screenWidth = getScreenWidth();
                    lp.width = (int) ((float) screenWidth * mRatio);
                }
            }
            lp.dimAmount = dimAmount;
            lp.y = mOffsetY;
            lp.gravity = mGravity;
            window.setAttributes(lp);
        }

        public Dialog create() {
            if (-1 == themeResId) {
                if (mBackgroundDimEnabled) {
                    themeResId = R.style.BaseDialogTheme;
                } else {
                    themeResId = R.style.BaseDialogTheme_BackgroundDimDisabled;
                }
            }
            mDialog = new Dialog(mContext, themeResId);
            if (layoutResID > 0) {
                mDialog.setContentView(layoutResID);
            } else if (null != mView) {
                mDialog.setContentView(mView);
            }
            windowSetting();
            return mDialog;
        }

        public Dialog show() {
            if (null == mDialog) {
                mDialog = create();
            }
            if (mDialog.isShowing()) {
                mDialog.show();
            }
            return mDialog;
        }
    }
}
