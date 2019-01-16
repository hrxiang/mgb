package com.hrxiang.android.net.request;

import com.hrxiang.android.net.core.HttpFactory;
import io.reactivex.Flowable;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/13.
 */
public final class GetRequest extends BaseRequest<GetRequest> {
    @Override
    protected <T> Flowable<T> readRemote(Type type) {
        return HttpFactory.getApiService().get(suffixUrl, params).compose(applySchedulers());
    }
}
