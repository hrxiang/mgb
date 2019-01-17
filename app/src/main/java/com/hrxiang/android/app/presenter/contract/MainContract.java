package com.hrxiang.android.app.presenter.contract;


import com.hrxiang.android.base.presenter.contract.BaseContract;

/**
 * Created by xianghairui on 2018/12/13.
 */
public interface MainContract {
    interface IView extends BaseContract.IView {
        void apiResult(String result);
    }

    interface IPresenter extends BaseContract.IPresenter {
        void apiTest();
    }
}
