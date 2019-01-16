package com.hrxiang.android.net.callback;

import java.util.Map;

/**
 * Created by xianghairui on 2018/12/12.
 */
public interface UploadProgressCallback<R> extends ICallback<R> {

    @Override
    default void onError(String requestUrl, Map<String, Object> requestParams, Throwable throwable) {

    }

    @Override
    default void onStart() {

    }

    @Override
    default void onFinish() {

    }
}
