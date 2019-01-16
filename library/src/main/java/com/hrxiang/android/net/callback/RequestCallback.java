package com.hrxiang.android.net.callback;

/**
 * Created by xianghairui on 2018/12/13.
 */
public interface RequestCallback<R> extends ICallback<R> {

    @Override
    default void onProgress(long currentLength, long totalLength, float percent) {
        //do nothing
    }
}
