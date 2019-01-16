package com.hrxiang.android.base.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
import com.hrxiang.android.base.utils.handler.BaseWeakReferenceHandler;


public class ObservableScrollView extends ScrollView {
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    public static final int SCROLL_STATE_FLING = 2;
    private OnScrollListener mOnScrollListener;
    private int mLastY;
    private StateHandler mStateHandler;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(OnScrollListener scrollListener) {
        this.mOnScrollListener = scrollListener;
        this.mStateHandler = new StateHandler(this);
    }

    public interface OnScrollListener {
        void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy);

        void onScrollStateChanged(ObservableScrollView view, int newState);

        void onScrolltoTop();

        void onScrolltoBottom();
    }

    private static class StateHandler extends BaseWeakReferenceHandler {

        private StateHandler(Object o) {
            super(o);
        }

        @Override
        public void onHandleMessage(Object o, Message msg) {
            if (o instanceof ObservableScrollView) {
                ObservableScrollView os = (ObservableScrollView) o;
                if (os.isEnabledScrollListener()) {
                    if (os.mLastY == os.getScrollY()) {
                        os.mOnScrollListener.onScrollStateChanged(os, SCROLL_STATE_IDLE);
                    } else {
                        os.mLastY = os.getScrollY();
                        os.mOnScrollListener.onScrollStateChanged(os, SCROLL_STATE_FLING);
                        sendEmptyMessageDelayed(0, 5);
                    }
                }
            }
        }

        @Override
        public void onException(Object o, Exception e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabledScrollListener()) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                mLastY = getScrollY();
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                mStateHandler.sendEmptyMessageDelayed(0, 5);
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                mLastY = getScrollY();
                if (isScrolltoBottom() || getScrollY() <= 0) {
                    mOnScrollListener.onScrollStateChanged(ObservableScrollView.this, SCROLL_STATE_IDLE);
                } else {
                    mOnScrollListener.onScrollStateChanged(ObservableScrollView.this, SCROLL_STATE_TOUCH_SCROLL);
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (isEnabledScrollListener()) {
            mOnScrollListener.onScrollChanged(this, x, y, oldx, oldy);
            if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
                mOnScrollListener.onScrolltoBottom();
            } else if (getScrollY() == 0) {
                mOnScrollListener.onScrolltoTop();
            } else {//other

            }
        }
    }

    private boolean isEnabledScrollListener() {
        return null != mOnScrollListener;
    }

    public boolean isScrolltoBottom() {
        return getScrollY() + getHeight() >= computeVerticalScrollRange();
    }
}
