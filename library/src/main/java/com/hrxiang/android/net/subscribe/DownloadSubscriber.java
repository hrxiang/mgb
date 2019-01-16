package com.hrxiang.android.net.subscribe;

import com.hrxiang.android.net.callback.ICallback;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.*;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class DownloadSubscriber<ResponseBody extends okhttp3.ResponseBody> implements Subscriber<ResponseBody> {
    private File file;
    private long lastRefreshUiTime;
    private ICallback<File> callback;

    public DownloadSubscriber(File file, ICallback<File> callback) {
        this.file = file;
        this.callback = callback;
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (null != callback) callback.onStart();
        s.request(1);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (null != callback) callback.onFinish();
        writeFileToDisk(responseBody);
    }

    @Override
    public void onError(Throwable t) {
        if (null != callback) callback.onError(null, null, t);
    }

    @Override
    public void onComplete() {
    }

    private void writeFileToDisk(okhttp3.ResponseBody resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[4096];

                inputStream = resp.byteStream();
                outputStream = new FileOutputStream(file);

                final long contentLength = resp.contentLength();

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    long curTime = System.currentTimeMillis();
                    //每200毫秒刷新一次数据,防止频繁更新进度
                    if (curTime - lastRefreshUiTime >= 400 || lastRefreshUiTime == 0
                            || downloadSize == contentLength) {
                        if (callback != null) {
                            final long currentLength = downloadSize;
                            Disposable d = Flowable.just(currentLength).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(@NonNull Long aLong) throws Exception {
                                            callback.onProgress(currentLength, contentLength, (100.0f * currentLength) / contentLength);
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(@NonNull Throwable throwable)
                                                throws Exception {
                                            callback.onError(null, null, throwable);
                                        }
                                    });
                        }
                        lastRefreshUiTime = System.currentTimeMillis();
                    }
                }
                outputStream.flush();
                if (null != callback) {
                    Disposable d = Flowable.just(file).observeOn(AndroidSchedulers.mainThread()).subscribe(
                            new Consumer<File>() {
                                @Override
                                public void accept(@NonNull File file) throws Exception {
                                    callback.onNext(null, null, file);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    callback.onError(null, null, throwable);
                                }
                            });
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
