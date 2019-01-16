package com.hrxiang.android.net.core.func;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class TimeoutRetryFunction extends RetryFunction {
    public TimeoutRetryFunction(int maxRetryCount, int retryDelayMillis) {
        super(maxRetryCount, retryDelayMillis);
    }

    @Override
    public boolean retry(Throwable throwable) {
        return throwable instanceof SocketTimeoutException || throwable instanceof ConnectException;
    }
}
