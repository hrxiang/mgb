package com.hrxiang.android.net.subscribe;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by xianghairui on 2018/12/12.
 */
public abstract class SimpleSubscribe<R> implements FlowableOnSubscribe<R> {
    @Override
    public void subscribe(FlowableEmitter<R> emitter) {
        try {
            R data = execute();
            if (null != data) emitter.onNext(data);
            emitter.onComplete();
        } catch (Exception e) {
            emitter.onError(e);
        }
    }

    public abstract R execute();
}
