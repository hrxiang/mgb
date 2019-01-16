package com.hrxiang.android.base.utils.EventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xianghairui on 2018/12/14.
 */
public final class EventCenter {
    public int code;
    public Object data;

    private EventCenter() {
    }

    private EventCenter(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    private static EventCenter newInstance(int code, Object data) {
        return new EventCenter(code, data);
    }

    public static void post(int code) {
        post(code, null);
    }

    public static void post(int code, Object data) {
        EventBus.getDefault().post(newInstance(code, data));
    }
}
