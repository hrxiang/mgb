package com.hrxiang.android.base.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianghairui on 2018/12/12.
 */
public final class JsonUtils {
    private static volatile ObjectMapper mapper;

    private JsonUtils() {
    }

    @Nullable
    public static String toString(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> T toObject(String jsonString, Class<T> clsBean) {
        try {
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }
            return getObjectMapper().readValue(jsonString, clsBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        try {
            if (TextUtils.isEmpty(jsonString)) {
                return null;
            }
            return getObjectMapper().readValue(jsonString, getCollectionType(ArrayList.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return getObjectMapper().getTypeFactory().constructParametricType(collectionClass,
                elementClasses);
    }

    private static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            synchronized (JsonUtils.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
                    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
                }
            }
        }
        return mapper;
    }
}
