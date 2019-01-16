package com.hrxiang.android.base.utils;

import android.os.CountDownTimer;

/**
 * Created by xianghairui on 2018/9/5
 */
public class CodeTimer {
    public static final String TAG = CodeTimer.class.getSimpleName();
    private boolean isRunning;//计时进行中
    private boolean isLastOperation;//接着完成上一次计时任务
    private long mMillisInFuture = 60 * 1000;//总时间（单位毫秒）
    private long mCountDownInterval = 1000;//递减值（单位毫秒）
    private String mIdentify;//timer 唯一标识
    private CodeCountDownTimer mLastTimeTimer;//上一次未进行完的定时器
    private CodeCountDownTimer mCurrentCodeTimer;//当前开启的定时器
    private CountDownCallback mCountDownCallback;


    private CodeTimer() {
    }

    /**
     * @param identify          唯一标识，因为会恢复未完成的定时任务，以此区分获取剩余时间。
     * @param millisInFuture    总时间
     * @param countDownInterval 每次减
     */
    public CodeTimer(String identify, long millisInFuture, long countDownInterval,
                     CountDownCallback callback) {
        this.mIdentify = TAG + "_" + identify;
        this.mMillisInFuture = millisInFuture + 1050;//
        this.mCountDownInterval = countDownInterval;
        this.mCountDownCallback = callback;
        checkLastOperation();
    }

    public void reset() {
        SpHelper.putObj(mIdentify, 0);
        if (null != mLastTimeTimer) {
            mLastTimeTimer.cancel();
            mLastTimeTimer.onFinish();
        }
        if (null != mCurrentCodeTimer) {
            mCurrentCodeTimer.cancel();
            mCurrentCodeTimer.onFinish();
        }
    }

    private void checkLastOperation() {
        long startTime = SpHelper.getObj(mIdentify, Long.class, 0L);
        long lave = mMillisInFuture - (System.currentTimeMillis() - startTime);
        if (lave > 0 && !isRunning) {//接着倒计时
            isLastOperation = true;
            isRunning = true;
            if (null == mLastTimeTimer) {
                mLastTimeTimer = new CodeCountDownTimer(lave, mCountDownInterval);
            }
            mLastTimeTimer.start();
        }
    }

    public void cancel() {
        if (null != mLastTimeTimer) {
            mLastTimeTimer.cancel();
        }
        if (null != mCurrentCodeTimer) {
            mCurrentCodeTimer.cancel();
        }
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            if (null != mLastTimeTimer) {
                mLastTimeTimer.cancel();
            }
            if (null == mCurrentCodeTimer) {
                mCurrentCodeTimer = new CodeCountDownTimer(mMillisInFuture, mCountDownInterval);
            }
            SpHelper.putObj(mIdentify, System.currentTimeMillis());
            mCurrentCodeTimer.start();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isLastOperation() {
        return isLastOperation;
    }

    private class CodeCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and
         *                          {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        private CodeCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (null != mCountDownCallback) {
                mCountDownCallback.onTick((millisUntilFinished / 1000));
            }
        }

        @Override
        public void onFinish() {
            isLastOperation = false;
            isRunning = false;
            if (null != mCountDownCallback) {
                mCountDownCallback.onFinish();
            }
        }
    }

    public interface CountDownCallback {
        void onTick(long secondUntilFinished);

        void onFinish();
    }
}
