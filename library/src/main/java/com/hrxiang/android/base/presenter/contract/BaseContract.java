package com.hrxiang.android.base.presenter.contract;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by xianghairui on 2018/12/13.
 */
public interface BaseContract {
    interface IPresenter extends LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        void onCreate(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void onStart(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStop(LifecycleOwner owner);

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy(LifecycleOwner owner);
    }

    interface IView {
        void showLoadingDialog();

        void hideLoadingDialog();

        <T> LifecycleTransformer<T> bindRxLifecycle();

        <V extends IView> V bindPresenterLifecycle(IPresenter presenter);
    }
}
