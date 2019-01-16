package com.hrxiang.android.net.cache.strategy;

import com.hrxiang.android.net.cache.DiskCache;
import io.reactivex.Flowable;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/10.
 */
public class OnlyCacheStrategy<R> implements ICacheStrategy<R> {

    @Override
    public Flowable<R> execute(DiskCache DiskCache, String key, long time, Type type, Flowable<R> source) {
        return readLocal(DiskCache, key, type);
    }
}
