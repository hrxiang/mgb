package com.hrxiang.android.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ViewFlipper;
import com.hrxiang.android.R;


public class VerticalScrollLayout extends ViewFlipper {

    private ListAdapter mAdapter;
    private boolean isSetAnimDuration = false;
    private int interval = 2000;
    /**
     * 动画时间
     */
    private int animDuration = 500;

    public VerticalScrollLayout(Context context) {
        this(context, null);
    }

    public VerticalScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalScrollLayout);
        animDuration = array.getInt(R.styleable.VerticalScrollLayout_vsl_animDuration, animDuration);
        isSetAnimDuration = array.getBoolean(R.styleable.VerticalScrollLayout_vsl_isCusDuration, false);
        interval = array.getInt(R.styleable.VerticalScrollLayout_vsl_sleepTime, interval);
        array.recycle();
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scroll_in);
        Animation animOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scroll_out);
        if (isSetAnimDuration) {
            animIn.setDuration(animDuration);
            animOut.setDuration(animDuration);
        }
        setInAnimation(animIn);
        setOutAnimation(animOut);
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            setupChildren();
        }

        @Override
        public void onInvalidated() {
            setupChildren();
        }

    };

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataObserver);
        }
        setupChildren();
    }

    private void setupChildren() {
        if (mAdapter == null || mAdapter.getCount() == 0) return;
        if (isFlipping()) {
            stopFlipping();
        }
        removeAllViews();
        new Handler().postDelayed(this::setView, 400);
    }

    private void setView() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View child = mAdapter.getView(i, null, this);
            if (child == null) {
                throw new NullPointerException("View can't be null");
            } else {
                addView(child);
            }
        }
        if (mAdapter.getCount() > 1) {
            startFlipping();
        }
    }

    public void resume() {
        if (getChildCount() > 1 && !isFlipping()) {
            startFlipping();
        }
    }

    public void pause() {
        if (isFlipping()) {
            stopFlipping();
        }
    }
}
