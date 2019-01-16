package com.hrxiang.android.net.request;

import com.hrxiang.android.net.cache.core.CacheMode;
import com.hrxiang.android.net.cache.strategy.*;
import com.hrxiang.android.net.callback.ICallback;
import com.hrxiang.android.net.core.HttpFactory;
import com.hrxiang.android.net.core.func.ApiFunction;
import com.hrxiang.android.net.core.func.RetryFunction;
import com.hrxiang.android.net.subscribe.CallbackSubscriber;
import com.hrxiang.android.net.utils.GsonFactory;
import com.hrxiang.android.net.utils.StringUtils;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import org.reactivestreams.Publisher;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xianghairui on 2018/12/13.
 */
public abstract class BaseRequest<Request extends BaseRequest> {
    String suffixUrl;
    Map<String, Object> params = new HashMap<>();
    private Type type;
    private boolean enableCache;
    private String cacheKey;
    private CacheMode cacheMode;// = CacheMode.CACHE_AND_REMOTE_DISTINCT
    private long cacheTime;
    private RetryFunction retryFunc;

    public <T> void execute(ICallback<T> callback, FlowableTransformer<T, T> lifecycle) {
        type = getType(callback);
        Flowable<T> f = canCache() ? checkCacheKey().readLocal(type) : readRemote(type);
        if (null != lifecycle) f = f.compose(lifecycle);
        f.subscribe(new CallbackSubscriber<T>(callback, suffixUrl, params));
    }

    protected abstract <T> Flowable<T> readRemote(Type type);

    protected <T> Flowable<T> readLocal(Type type) {
        return this.<T>readRemote(type).compose(applyCacheStrategy());
    }

    public Request setSuffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
        return (Request) this;
    }

    public Request setParams(Map<String, Object> params) {
        this.params = params;
        return (Request) this;
    }

    public Request addParams(String key, Object value) {
        this.params.put(key, value);
        return (Request) this;
    }

    public Request useCacheStrategy() {
        this.enableCache = true;
        return (Request) this;
    }

    public Request setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (Request) this;
    }

    public Request setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (Request) this;
    }

    public Request setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return (Request) this;
    }

    public Request setRetryWhen(RetryFunction retryFunc) {
        this.retryFunc = retryFunc;
        return (Request) this;
    }

    private boolean canCache() {
        return enableCache && null != HttpFactory.getDiskCache();
    }

    private BaseRequest checkCacheKey() {
        this.cacheKey = StringUtils.isEmpty(cacheKey) ? suffixUrl + "?params=" + GsonFactory.create().toJson(params) : cacheKey;
        return this;
    }

    <R> FlowableTransformer<ResponseBody, R> applySchedulers() {
        return new FlowableTransformer<ResponseBody, R>() {
            @Override
            public Publisher<R> apply(Flowable<ResponseBody> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(retryFunc == null ? new NotRetryFunc() : retryFunc)
                        .map(new ApiFunction<>(type));
            }
        };
    }

    private <R> FlowableTransformer<R, R> applyCacheStrategy() {
        final ICacheStrategy<R> strategy = loadStrategy(cacheMode);//获取缓存策略
        return new FlowableTransformer<R, R>() {
            @Override
            public Publisher<R> apply(Flowable<R> upstream) {
                return strategy.execute(HttpFactory.getDiskCache(), cacheKey, cacheTime, type, upstream);
            }
        };
    }

    private static <T> ICacheStrategy<T> loadStrategy(CacheMode cacheMode) {
        ICacheStrategy<T> strategy;
        if (CacheMode.FIRST_REMOTE.equals(cacheMode)) {
            strategy = new FirstRemoteStrategy<T>();
        } else if (CacheMode.FIRST_CACHE.equals(cacheMode)) {
            strategy = new FirstCacheStrategy<T>();
        } else if (CacheMode.ONLY_REMOTE.equals(cacheMode)) {
            strategy = new OnlyRemoteStrategy<T>();
        } else if (CacheMode.ONLY_CACHE.equals(cacheMode)) {
            strategy = new OnlyCacheStrategy<T>();
        } else if (CacheMode.CACHE_AND_REMOTE.equals(cacheMode)) {
            strategy = new CacheAndRemoteStrategy<T>();
        } else {//(CacheMode.CACHE_AND_REMOTE_DISTINCT.equals(cacheMode))
            strategy = new CacheAndRemoteDistinctStrategy<T>();
        }
        return strategy;
        /*try {
            String pkName = ICacheStrategy.class.getPackage().getName();
            return (ICacheStrategy) Class.forName(pkName + "." + cacheMode.getClassName()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("loadStrategy(" + cacheMode + ") err!!" + e.getMessage());
        }*/
    }

    /**
     * 获取第一级type
     */
    protected <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType))
                throw new IllegalStateException("generic parameters error");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 获取次一级type(如果有)
     */
    protected <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType))
                throw new IllegalStateException("generic parameters error");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }

    private static final class NotRetryFunc extends RetryFunction {
        private NotRetryFunc() {
            super(1, 3000);
        }

        @Override
        public boolean retry(Throwable throwable) {
            return false;
        }
    }

}
