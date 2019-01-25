package com.hrxiang.android.base.presenter;

import androidx.lifecycle.LifecycleOwner;
import com.hrxiang.android.base.presenter.contract.BaseContract;

import java.lang.ref.WeakReference;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class BasePresenter<View extends BaseContract.IView> implements BaseContract.IPresenter {
    private WeakReference<View> mReference;

    public BasePresenter() {
    }

    public BasePresenter(View view) {
        if (null != view) {
            mReference = new WeakReference<>(view.bindPresenterLifecycle(this));
        }
    }

    public <Presenter extends BasePresenter> Presenter onAttach(View view) {
        if (null != view && !isAttach()) {
            mReference = new WeakReference<>(view.bindPresenterLifecycle(this));
        }
        return (Presenter) this;
    }

    public View getView() {
        if (isAttach()) {
            return mReference.get();
        }
        return null;
    }

    public void onDetach() {
        if (isAttach()) {
            mReference.clear();
            mReference = null;
        }
    }

    private boolean isAttach() {
        return null != mReference && null != mReference.get();
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
    }

    @Override
    public void onStart(LifecycleOwner owner) {
    }

    @Override
    public void onResume(LifecycleOwner owner) {
    }

    @Override
    public void onStop(LifecycleOwner owner) {
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        onDetach();
    }
}

