package com.hrxiang.android.net.cache.strategy;

import com.hrxiang.android.net.cache.DiskCache;
import io.reactivex.Flowable;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/10.
 * 先加载缓存，缓存没有再去请求网络
 */
public class FirstCacheStrategy<R>  implements ICacheStrategy<R> {

    @Override
    public Flowable<R> execute(DiskCache DiskCache, String key, long time, Type type, Flowable<R> source) {
        Flowable<R> cache = readLocal(DiskCache, key, type);
        Flowable<R> remote = readRemote(DiskCache, key, time, source);
        return cache.switchIfEmpty(remote);
    }
}
