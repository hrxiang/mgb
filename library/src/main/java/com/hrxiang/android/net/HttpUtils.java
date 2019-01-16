package com.hrxiang.android.net;

import com.hrxiang.android.net.request.DownloadRequest;
import com.hrxiang.android.net.request.GetRequest;
import com.hrxiang.android.net.request.PostRequest;
import com.hrxiang.android.net.request.UploadRequest;

/**
 * Created by xianghairui on 2018/12/13.
 */
public final class HttpUtils {

    public static GetRequest get() {
        return new GetRequest();
    }

    public static PostRequest post() {
        return new PostRequest();
    }

    public static DownloadRequest download() {
        return new DownloadRequest();
    }

    public static UploadRequest upload() {
        return new UploadRequest();
    }
}
