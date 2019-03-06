package com.hrxiang.android.net.cache;

import com.hrxiang.android.net.cache.core.ICache;
import com.hrxiang.android.net.subscribe.SimpleSubscribe;
import com.hrxiang.android.net.utils.AesUtils;
import com.hrxiang.android.net.utils.GsonFactory;
import com.hrxiang.android.net.utils.Md5Utils;
import com.hrxiang.android.net.utils.StringUtils;
import com.jakewharton.disklrucache.DiskLruCache;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xianghairui on 2018/12/12.
 */
public final class DiskCache implements ICache {
    private static final long CACHE_NEVER_EXPIRE = -1;//永久不过期
    private static final String REGEX = "@createTime\\{(\\d+)\\}expireMills\\{((-)?\\d+)\\}@";
    private static final String TAG_CACHE = "@createTime{createTime_v}expireMills{expireMills_v}@";
    private DiskLruCache cache;
    private Pattern compile;
    private File cacheDir;
    private int appVersion;
    private long maxSize;

    private DiskCache() {
    }

    public void put(String key, String value, long time) {
        try {
            if (null == cache) return;
            key = Md5Utils.md5(key);
            value = AesUtils.encrypt(key, value);
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) return;
            time = time == 0 ? CACHE_NEVER_EXPIRE : time;
            cache.remove(key);
            DiskLruCache.Editor editor = cache.edit(key);
            String content = value + TAG_CACHE
                    .replace("createTime_v", String.valueOf(System.currentTimeMillis()))
                    .replace("expireMills_v", String.valueOf(time));
            editor.set(0, content);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, Object value, long time) {
        put(key, null != value ? GsonFactory.create().toJson(value) : null, time);
    }

    @Override
    public String get(String key) {
        try {
            if (null == cache) return null;
            key = Md5Utils.md5(key);
            if (null == key) return null;
            DiskLruCache.Snapshot snapshot = cache.get(key);
            if (null != snapshot) {
                String content = snapshot.getString(0);
                if (!StringUtils.isEmpty(content)) {
                    Matcher matcher = compile.matcher(content);
                    long createTime = 0;
                    long expireMills = 0;
                    while (matcher.find()) {
                        createTime = Long.parseLong(matcher.group(1));
                        expireMills = Long.parseLong(matcher.group(2));
                    }
                    int index = content.indexOf("@createTime");
                    if (createTime + expireMills > System.currentTimeMillis()
                            || expireMills == CACHE_NEVER_EXPIRE) {
                        return AesUtils.decrypt(key, content.substring(0, index));
                    } else {
                        cache.remove(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        try {
            if (null != cache) {
                key = Md5Utils.md5(key);
                return null != key && null != cache.get(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void remove(String key) {
        try {
            if (null != cache) {
                key = Md5Utils.md5(key);
                if (null != key) {
                    cache.remove(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            if (null != cache)
                cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    DiskCache(Builder builder) {
        this.cacheDir = builder.cacheDir;
        this.appVersion = builder.appVersion;
        this.maxSize = builder.maxSize;
        this.compile = Pattern.compile(REGEX);
        Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                cache = DiskLruCache.open(cacheDir, appVersion, 1, maxSize);
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).subscribe();
    }

    public final static class Builder {
        private File cacheDir;
        private int appVersion;
        private long maxSize;

        public Builder() {
            //default config
        }

        Builder(DiskCache diskCache) {
            this.cacheDir = diskCache.cacheDir;
            this.appVersion = diskCache.appVersion;
            this.maxSize = diskCache.maxSize;
        }

        public Builder cacheDir(File dir) {
            this.cacheDir = dir;
            return this;
        }

        public Builder appVersion(int version) {
            this.appVersion = version;
            return this;
        }

        public Builder maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public DiskCache build() {
            return new DiskCache(this);
        }
    }

    public void writeCache(String key, Object value, long time) {
        Flowable
                .create((FlowableOnSubscribe<Boolean>) emitter -> put(key, value, time), BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Flowable<String> readCache(String key) {
        return Flowable.create(new SimpleSubscribe<String>() {
            @Override
            public String execute() {
                return get(key);
            }
        }, BackpressureStrategy.BUFFER);
    }
}
