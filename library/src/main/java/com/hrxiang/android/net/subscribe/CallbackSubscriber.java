package com.hrxiang.android.net.subscribe;

import com.hrxiang.android.net.callback.ICallback;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class CallbackSubscriber<R> implements Subscriber<R> {
    private ICallback<R> callback;
    private String requestUrl;
    private Map<String, Object> requestParams;
    private Subscription subscription;

    public CallbackSubscriber(ICallback<R> callback, String requestUrl, Map<String, Object> requestParams) {
        this.callback = callback;
        this.requestUrl = requestUrl;
        this.requestParams = requestParams;
    }

    @Override
    public void onError(Throwable t) {
        if (null != callback) {
            callback.onError(requestUrl, requestParams, t);
            callback.onFinish();
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscription = s;
        if (null != callback) callback.onStart();
        s.request(1);
    }

    @Override
    public void onNext(R r) {
        if (null != callback) {
            callback.onNext(requestUrl, requestParams, r);
            callback.onFinish();
        }
    }

    public Subscription getSubscription() {
        return subscription;
    }
}
