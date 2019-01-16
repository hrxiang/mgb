package com.hrxiang.android.net.cache.core;

/**
 * Created by xianghairui on 2018/12/12.
 */
public interface ICache {
    void put(String key, Object value, long time);

    String get(String key);

    boolean contains(String key);

    void remove(String key);

    void clear();
}
