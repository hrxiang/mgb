package com.hrxiang.android.base.utils.handler;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by hairui.xiang on 2017/9/27.
 * 1，捕获异常
 * 2，使用activity或者fragment的弱引用，防止因handler延时操作造成内存泄漏
 */
public abstract class WeakReferenceHandler<Hold> extends Handler {
    public abstract void handleMessage(Hold hold, Message msg);

    public abstract void onException(Hold hold, Exception e);

    private WeakReference<Hold> reference;

    public WeakReferenceHandler(Hold hold) {
        reference = new WeakReference<>(hold);
    }

    public Hold getHold() {
        return null != reference ? reference.get() : null;
    }

    @Override
    public final void handleMessage(Message msg) {
        Hold hold = null;
        try {
            hold = reference.get();
            if (null != hold && null != msg) {
                handleMessage(hold, msg);
            }
        } catch (Exception e) {
            onException(hold, e);
        }
    }
}
