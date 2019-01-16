package com.hrxiang.android.net.cache.strategy;

import com.hrxiang.android.net.cache.DiskCache;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okio.ByteString;

import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/6.
 * *先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，
 * 等网络请求回来发现数据是一样的就不会再返回，否则再返回
 * （这样做的目的是防止数据是一样的你也需要刷新界面）
 */
public class CacheAndRemoteDistinctStrategy<R> implements ICacheStrategy<R> {

    @Override
    public Flowable<R> execute(DiskCache DiskCache, String key, long time, Type type, Flowable<R> source) {
        Flowable<R> cache = readLocal(DiskCache, key, type);
        Flowable<R> remote = readRemote(DiskCache, key, time, source);

        return Flowable.concat(cache, remote).filter(new Predicate<R>() {
            @Override
            public boolean test(R r) throws Exception {
                return null != r;
            }
        }).distinctUntilChanged(new Function<R, String>() {
            @Override
            public String apply(R r) throws Exception {
                return ByteString.of(r.toString().getBytes()).md5().hex();
            }
        });
    }
}
