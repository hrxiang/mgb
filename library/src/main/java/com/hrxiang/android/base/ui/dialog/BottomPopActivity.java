package com.hrxiang.android.base.ui.dialog;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hrxiang.android.R;
import com.hrxiang.android.base.ui.activity.BaseActivity;

/**
 * Created by xianghairui on 2018/12/14.
 */
public abstract class BottomPopActivity/*<V extends BaseContract.IView, P extends BasePresenter> */ extends BaseActivity/*<V, P>*/ {
    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;
    protected View mBg;
    protected FrameLayout mPanel;

    protected boolean canScroll() {
        return true;
    }

    @Override
    protected void bindButterKnife() {
        mBg = findViewById(R.id.id_pop_bg);
        mPanel = findViewById(R.id.id_pop_panel);
        mPanel.addView(View.inflate(this, getDialogLayoutId(), null));
        super.bindButterKnife();
    }

    @Override
    protected final void initWidgetAndEvent() {
        mBg.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());
    }

    public abstract int getDialogLayoutId();

    @Override
    protected final int getContentLayoutId() {
        return canScroll() ? R.layout.bottom_pop_dialog_ablescroll : R.layout.bottom_pop_dialog_disablescroll;
    }

    @Override
    protected final boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void initImmersionBar() {
//    super.initImmersionBar();
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
                .statusBarDarkFont(true)
                .keyboardEnable(true,
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();
    }

    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    public final void dismiss() {
        mBg.startAnimation(createAlphaOutAnimation());
        mPanel.startAnimation(createTranslationOutAnimation());
        new Handler().postDelayed(this::finish, 200);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
