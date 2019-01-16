package com.hrxiang.android.net.cache.strategy;

import com.hrxiang.android.net.cache.DiskCache;
import com.hrxiang.android.net.cache.core.ICacheFilter;
import com.hrxiang.android.net.utils.GsonFactory;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/12.
 */
public interface ICacheStrategy<R> {

    Flowable<R> execute(DiskCache DiskCache, String key, long time, Type type, Flowable<R> source);

    default Flowable<R> readLocal(DiskCache diskCache, String key, Type type) {
        return diskCache.readCache(key).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return null != s;
            }
        }).map(new Function<String, R>() {
            @Override
            public R apply(String s) throws Exception {
                return GsonFactory.create().fromJson(s, type);
            }
        });
    }


    default Flowable<R> readRemote(DiskCache diskCache, String key, long time, Flowable<R> source) {
        return source.map(new Function<R, R>() {
            @Override
            public R apply(R r) throws Exception {
                if (r instanceof ICacheFilter) {
                    if (((ICacheFilter) r).accept()) {
                        diskCache.writeCache(key, r, time);
                    }
                } else {
                    diskCache.writeCache(key, r, time);
                }
                return r;
            }
        });
    }
}
