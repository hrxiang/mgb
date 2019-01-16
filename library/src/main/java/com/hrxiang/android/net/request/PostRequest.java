package com.hrxiang.android.net.request;

import com.hrxiang.android.net.core.HttpFactory;
import com.hrxiang.android.net.core.MediaTypes;
import com.hrxiang.android.net.utils.GsonFactory;
import io.reactivex.Flowable;
import okhttp3.RequestBody;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/13.
 */
public final class PostRequest extends BaseRequest<PostRequest> {
    private boolean postJson;

    @Override
    protected <T> Flowable<T> readRemote(Type type) {
        if (postJson) {
            return HttpFactory
                    .getApiService()
                    .postBody(suffixUrl, RequestBody.create(MediaTypes.APPLICATION_JSON_TYPE, GsonFactory.create().toJson(params)))
                    .compose(applySchedulers());
        }
        return HttpFactory.getApiService().post(suffixUrl, params).compose(applySchedulers());
    }

    public PostRequest setPostJson(boolean postJson) {
        this.postJson = postJson;
        return this;
    }
}
