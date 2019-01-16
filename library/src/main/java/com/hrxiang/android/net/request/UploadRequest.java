package com.hrxiang.android.net.request;

import android.support.annotation.NonNull;
import com.hrxiang.android.net.body.UploadProgressRequestBody;
import com.hrxiang.android.net.callback.UploadProgressCallback;
import com.hrxiang.android.net.core.HttpFactory;
import com.hrxiang.android.net.core.MediaTypes;
import com.hrxiang.android.net.utils.GsonFactory;
import io.reactivex.Flowable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xianghairui on 2018/12/12.
 */
public final class UploadRequest extends BaseRequest<UploadRequest> {

    private List<MultipartBody.Part> parts = new ArrayList<>();

    @Override
    protected <T> Flowable<T> readRemote(Type type) {
        if (!params.isEmpty()) {
            Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
            Map.Entry<String, Object> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (null != entry && null != entry.getKey() && null != entry.getValue()) {
                    parts.add(MultipartBody.Part.createFormData(entry.getKey(), GsonFactory.create().toJson(entry.getValue())));
                }
            }
        }
        return HttpFactory.getApiService().uploadFiles(suffixUrl, parts).compose(applySchedulers());
    }

    public UploadRequest addFile(String key, File file) {
        return addFile(key, file, null);
    }

    public UploadRequest addFile(String key, File file, UploadProgressCallback callback) {
        if (null == key || null == file) return this;
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
        if (null != callback) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            this.parts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            this.parts.add(part);
        }
        return this;
    }

    public UploadRequest addImageFile(String key, File file) {
        return addImageFile(key, file, null);
    }

    public UploadRequest addImageFile(String key, File file, UploadProgressCallback callback) {
        if (null == key || null == file) return this;
        RequestBody requestBody = RequestBody.create(MediaTypes.IMAGE_TYPE, file);
        if (null != callback) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            this.parts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            this.parts.add(part);
        }
        return this;
    }

    public UploadRequest addBytes(String name, String filename, byte[] bytes) {
        return addBytes(name, filename, bytes, null);
    }

    public UploadRequest addBytes(String name, String filename, byte[] bytes, UploadProgressCallback callback) {
        if (null == name || null == bytes || null == filename) return this;
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, bytes);
        if (null != callback) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, filename, uploadProgressRequestBody);
            this.parts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, filename, requestBody);
            this.parts.add(part);
        }
        return this;
    }

    public UploadRequest addStream(String name, String filename, InputStream inputStream) {
        return addStream(name, filename, inputStream, null);
    }

    public UploadRequest addStream(String name, String filename, InputStream inputStream, UploadProgressCallback callback) {
        if (null == name || null == inputStream || null == filename) return this;

        RequestBody requestBody = create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, inputStream);
        if (null != callback) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, filename, uploadProgressRequestBody);
            this.parts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(name, filename, requestBody);
            this.parts.add(part);
        }
        return this;
    }

    private static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }
}
