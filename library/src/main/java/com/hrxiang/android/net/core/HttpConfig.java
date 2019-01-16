package com.hrxiang.android.net.core;

import okhttp3.Cache;
import okhttp3.Interceptor;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianghairui on 2018/12/21.
 */
public final class HttpConfig {
    public static final long DEFAULT_TIME = 30L;
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

    HttpConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.writeTimeout = builder.writeTimeout == 0 ? DEFAULT_TIME : builder.writeTimeout;
        this.readTimeout = builder.readTimeout == 0 ? DEFAULT_TIME : builder.readTimeout;
        this.connectTimeout = builder.connectTimeout == 0 ? DEFAULT_TIME : builder.connectTimeout;
        this.cache = builder.cache;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.trustManager = builder.trustManager;
        this.interceptors.addAll(builder.interceptors);
        this.networkInterceptors.addAll(builder.networkInterceptors);
        this.cacheDir = builder.cacheDir;
        this.appVersion = builder.appVersion;
        this.maxSize = builder.maxSize;
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

        Builder(HttpConfig config) {
            this.baseUrl = config.baseUrl;
            this.writeTimeout = config.writeTimeout;
            this.readTimeout = config.readTimeout;
            this.connectTimeout = config.connectTimeout;
            this.cache = config.cache;
            this.interceptors.addAll(config.interceptors);
            this.networkInterceptors.addAll(config.networkInterceptors);
            this.sslSocketFactory = config.sslSocketFactory;
            this.trustManager = config.trustManager;
            this.cacheDir = config.cacheDir;
            this.appVersion = config.appVersion;
            this.maxSize = config.maxSize;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setInterceptors(List<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public Builder setNetworkInterceptors(List<Interceptor> networkInterceptors) {
            this.networkInterceptors = networkInterceptors;
            return this;
        }

        public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder setTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
            return this;
        }

        public Builder setCacheDir(File cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public Builder setAppVersion(int appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setMaxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public HttpConfig build() {
            return new HttpConfig(this);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public Cache getCache() {
        return cache;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public List<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public long getMaxSize() {
        return maxSize;
    }
}
