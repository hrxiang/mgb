package com.hrxiang.android.net.interceptor;

import com.hrxiang.android.BuildConfig;
import com.hrxiang.android.base.utils.FileUtils;
import com.hrxiang.android.net.core.MediaTypes;
import okhttp3.*;

import java.io.IOException;

/**
 * Created by xianghairui on 2019/4/10.
 * 1，每一个接口的响应内容对应一个json文件
 * 2，每一个json文件都以url作为文件名
 * 3，json文件放到assets下
 */
public class MockInterceptor implements Interceptor {
    private static final String BUILD_TYPE = "mock";

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (BUILD_TYPE.equalsIgnoreCase(BuildConfig.BUILD_TYPE)) {
            String responseMessage = createResponseMessage(chain);
            return new Response.Builder()
                    .code(200)
                    .message(responseMessage)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .addHeader("content-type", "application/json")
                    .body(ResponseBody.create(MediaTypes.APPLICATION_JSON_TYPE, responseMessage.getBytes()))
                    .build();
        }
        return chain.proceed(chain.request());
    }

    private String createResponseMessage(Chain chain) {
        HttpUrl uri = chain.request().url();
        String path = uri.url().getPath();
        return getResponseJsonByPath(path);
    }

    private static String getResponseJsonByPath(String fileName) {
        try {
            return FileUtils.readJsonFromAssetsFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
