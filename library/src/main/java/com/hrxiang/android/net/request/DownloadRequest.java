package com.hrxiang.android.net.request;

import com.hrxiang.android.net.callback.DownloadProgressCallback;
import com.hrxiang.android.net.callback.ICallback;
import com.hrxiang.android.net.core.HttpFactory;
import com.hrxiang.android.net.subscribe.DownloadSubscriber;
import com.hrxiang.android.net.utils.StringUtils;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/13.
 */
public final class DownloadRequest extends BaseRequest<DownloadRequest> {
    private File dir;
    private String filename;

    @Deprecated
    @Override
    public <T> void execute(ICallback<T> callback, FlowableTransformer<T, T> lifecycle) {
    }

    public void execute(DownloadProgressCallback callback) {

        if (null == dir || StringUtils.isEmpty(filename)) {
            throw new NullPointerException("dir or filename is null");
        }

        HttpFactory
                .getApiService()
                .downloadFile(suffixUrl, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadSubscriber<ResponseBody>(new File(dir, filename), callback));
    }

    @Override
    protected <T> Flowable<T> readRemote(Type type) {
        return null;
    }

    public DownloadRequest setDir(File dir) {
        this.dir = dir;
        return this;
    }

    public DownloadRequest setFilename(String filename) {
        this.filename = filename;
        return this;
    }
}
