package com.hrxiang.android.net.callback;

import java.util.Map;

/**
 * Created by xianghairui on 2018/12/13.
 */
public interface ICallback<R> {
    void onNext(String requestUrl, Map<String, Object> requestParams, R responseResult);

//    void onSuccess(String requestUrl, Map<String, Object> requestParams, R responseResult);
//
//    void onFailure(String requestUrl, Map<String, Object> requestParams, R responseResult);

    void onError(String requestUrl, Map<String, Object> requestParams, Throwable throwable);

    void onProgress(long currentLength, long totalLength, float percent);

    void onStart();

    void onFinish();
}
