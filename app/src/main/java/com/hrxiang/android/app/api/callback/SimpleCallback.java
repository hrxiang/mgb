package com.hrxiang.android.app.api.callback;


import com.hrxiang.android.base.presenter.contract.BaseContract;
import com.hrxiang.android.net.callback.RequestCallback;

import java.lang.ref.WeakReference;

/**
 * Created by xianghairui on 2018/12/13.
 */
public abstract class SimpleCallback<R> implements RequestCallback<R> {
    private WeakReference<BaseContract.IView> mReference;

    public SimpleCallback(BaseContract.IView view) {
        this.mReference = new WeakReference<>(view);
    }

    @Override
    public void onStart() {
        if (null != mReference.get()) {
            mReference.get().showLoadingDialog();
        }
    }

    @Override
    public void onFinish() {
        if (null != mReference.get()) {
            mReference.get().hideLoadingDialog();
        }
    }
}
