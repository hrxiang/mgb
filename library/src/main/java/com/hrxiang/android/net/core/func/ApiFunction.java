package com.hrxiang.android.net.core.func;

import com.hrxiang.android.net.utils.GsonFactory;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class ApiFunction<T> implements Function<ResponseBody, T> {
    private Type type;

    public ApiFunction(Type type) {
        this.type = type;
    }

    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        String json;
        try {
            json = responseBody.string();
            if (type.equals(String.class)) {
                return (T) json;
            } else {
                return GsonFactory.create().fromJson(json, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        return null;
    }
}
