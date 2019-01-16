package com.hrxiang.android.net.core;

import com.hrxiang.android.net.api.ApiService;
import com.hrxiang.android.net.cache.DiskCache;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xianghairui on 2018/12/12.
 */
public final class HttpFactory {
    private String baseUrl;
    private long writeTimeout;
    private long readTimeout;
    private long connectTimeout;
    private Cache cache;
    private List<Interceptor> interceptors = new ArrayList<>();
    private List<Interceptor> networkInterceptors = new ArrayList<>();
    private SSLSocketFactory sslSocketFactory;
    private X509TrustManager trustManager;
    private File cacheDir;
    private int appVersion;
    private long maxSize;
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;
    private static DiskCache diskCache;
    private static ApiService apiService;
    private static HttpConfig httpConfig;

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static DiskCache getDiskCache() {
        return diskCache;
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public static HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public static <T> T createCustomService(final Class<T> service) {
        return retrofit.create(service);
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    private HttpFactory(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.writeTimeout = builder.writeTimeout == 0 ? HttpConfig.DEFAULT_TIME : builder.writeTimeout;
        this.readTimeout = builder.readTimeout == 0 ? HttpConfig.DEFAULT_TIME : builder.readTimeout;
        this.connectTimeout = builder.connectTimeout == 0 ? HttpConfig.DEFAULT_TIME : builder.connectTimeout;
        this.cache = builder.cache;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.trustManager = builder.trustManager;
        this.interceptors.addAll(builder.interceptors);
        this.networkInterceptors.addAll(builder.networkInterceptors);
        this.cacheDir = builder.cacheDir;
        this.appVersion = builder.appVersion;
        this.maxSize = builder.maxSize;

        httpConfig = new HttpConfig.Builder()
                .setBaseUrl(baseUrl)
                .setWriteTimeout(writeTimeout)
                .setReadTimeout(readTimeout)
                .setConnectTimeout(connectTimeout)
                .setCache(cache)
                .setSslSocketFactory(sslSocketFactory)
                .setTrustManager(trustManager)
                .setInterceptors(interceptors)
                .setNetworkInterceptors(networkInterceptors)
                .setCacheDir(cacheDir)
                .setAppVersion(appVersion)
                .setMaxSize(maxSize)
                .build();

        if (null != httpConfig.getCacheDir() && httpConfig.getMaxSize() > 0) {
            diskCache = new DiskCache.Builder()
                    .cacheDir(httpConfig.getCacheDir())
                    .appVersion(httpConfig.getAppVersion())
                    .maxSize(httpConfig.getMaxSize())
                    .build();
        }

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS)
//                .cache(cache)
                .retryOnConnectionFailure(true);


        for (Interceptor i : httpConfig.getInterceptors()) {
            okHttpBuilder.addInterceptor(i);
        }

        for (Interceptor i : httpConfig.getNetworkInterceptors()) {
            okHttpBuilder.addInterceptor(i);
        }

        if (null != httpConfig.getTrustManager() && null != httpConfig.getSslSocketFactory()) {
            okHttpBuilder.sslSocketFactory(httpConfig.getSslSocketFactory(), httpConfig.getTrustManager());
        }

        okHttpClient = okHttpBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(httpConfig.getBaseUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static final class Builder {
        private String baseUrl;
        private long writeTimeout;
        private long readTimeout;
        private long connectTimeout;
        private Cache cache;
        private List<Interceptor> interceptors = new ArrayList<>();
        private List<Interceptor> networkInterceptors = new ArrayList<>();
        private SSLSocketFactory sslSocketFactory;
        private X509TrustManager trustManager;
        private File cacheDir;
        private int appVersion;
        private long maxSize;

        public Builder() {
            //default config
        }

        Builder(HttpFactory factory) {
            this.baseUrl = factory.baseUrl;
            this.writeTimeout = factory.writeTimeout;
            this.readTimeout = factory.readTimeout;
            this.connectTimeout = factory.connectTimeout;
            this.cache = factory.cache;
            this.interceptors.addAll(factory.interceptors);
            this.networkInterceptors.addAll(factory.networkInterceptors);
            this.sslSocketFactory = factory.sslSocketFactory;
            this.trustManager = factory.trustManager;
            this.cacheDir = factory.cacheDir;
            this.appVersion = factory.appVersion;
            this.maxSize = factory.maxSize;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder writeTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder readTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder connectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder addInterceptors(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder addNetworkInterceptors(Interceptor interceptor) {
            this.networkInterceptors.add(interceptor);
            return this;
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            this.sslSocketFactory = sslSocketFactory;
            this.trustManager = trustManager;
            return this;
        }

        public Builder cacheDir(File dir) {
            this.cacheDir = dir;
            return this;
        }

        public Builder appVersion(int version) {
            this.appVersion = version;
            return this;
        }

        /**
         * @param maxSize cache bytes
         */
        public Builder maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public HttpFactory build() {
            return new HttpFactory(this);
        }
    }
}
