package com.hrxiang.android.net.api;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by xianghairui on 2018/12/13.
 */
public interface ApiService {
    @GET()
    Flowable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    @FormUrlEncoded
    @POST()
    Flowable<ResponseBody> post(@Url() String url, @FieldMap Map<String, Object> maps);

    @POST()
    Flowable<ResponseBody> postBody(@Url() String url, @Body RequestBody requestBody);

    @HEAD()
    Flowable<ResponseBody> head(@Url String url, @QueryMap Map<String, Object> maps);

    @OPTIONS()
    Flowable<ResponseBody> options(@Url String url, @QueryMap Map<String, Object> maps);

    @FormUrlEncoded
    @PUT()
    Flowable<ResponseBody> put(@Url() String url, @FieldMap Map<String, Object> maps);

    @FormUrlEncoded
    @PATCH()
    Flowable<ResponseBody> patch(@Url() String url, @FieldMap Map<String, Object> maps);

    @FormUrlEncoded
    @DELETE()
    Flowable<ResponseBody> delete(@Url() String url, @FieldMap Map<String, Object> maps);

    @Streaming
    @GET()
    Flowable<ResponseBody> downloadFile(@Url() String url, @QueryMap Map<String, Object> maps);

    @Multipart
    @POST()
    Flowable<ResponseBody> uploadFiles(@Url() String url, @Part() List<MultipartBody.Part> parts);
}
