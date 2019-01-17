package com.hrxiang.android.app.presenter;

import com.hrxiang.android.app.api.callback.SimpleCallback;
import com.hrxiang.android.app.presenter.contract.MainContract;
import com.hrxiang.android.base.presenter.BasePresenter;
import com.hrxiang.android.net.HttpUtils;
import com.hrxiang.android.net.cache.core.CacheMode;

import java.util.Map;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class MainPresenter extends BasePresenter<MainContract.IView> implements MainContract.IPresenter {

    public MainPresenter() {
    }

    public MainPresenter(MainContract.IView view) {
        super(view);
    }

    public void apiTest() {
        HttpUtils
                .get()
                .setSuffixUrl("weatherApi")
                .addParams("city", "成都")
                .useCacheStrategy()
                .setCacheMode(CacheMode.FIRST_REMOTE)
                .execute(new SimpleCallback<String>(getView()) {
                    @Override
                    public void onNext(String requestUrl, Map<String, Object> requestParams, String responseResult) {
                        if (null != getView()) getView().apiResult(responseResult);
                    }

                    @Override
                    public void onError(String requestUrl, Map<String, Object> requestParams, Throwable throwable) {

                    }
                }, getView().bindRxLifecycle());
    }
}
