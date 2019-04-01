package com.hrxiang.android.base.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hrxiang.android.R;
import com.hrxiang.android.base.presenter.BasePresenter;
import com.hrxiang.android.base.presenter.contract.BaseContract;
import com.hrxiang.android.base.utils.EventBus.EventCenter;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class BaseFragment
        /*<V extends BaseContract.IView, Presenter extends BasePresenter>*/
        extends PermissionsFragment implements BaseContract.IView {
    /*protected Presenter mPresenter;*/
    private boolean isOnViewCreated;
    private boolean isVisible;
    private boolean isLoaded;

    protected View mFragmentView;
    protected View mRootView;
    protected View mStatusBarView;
    private FrameLayout mHeaderParentView;
    private FrameLayout mContentParentView;
    private Dialog mLoadingDialog;
    private View mLoadingView;
    protected ImmersionBar mImmersionBar;

/*
    protected Presenter createPresenter() {
        try {
            if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
                if (null != types && types.length >= 2) {
                    Type type = types[1];
                    Class<Presenter> entityClass = (Class<Presenter>) type;
                    return entityClass.newInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void attachPresenter() {
        mPresenter = createPresenter();
        if (null != mPresenter) {
            mPresenter.onAttach(this);
            registerPresenterLifecycle(mPresenter);
        }
    }
*/

    protected void createPresenters() {
    }

    @Override
    public <T> LifecycleTransformer<T> bindRxLifecycle() {
        return bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }

    @Override
    public <V extends BaseContract.IView> V bindPresenterLifecycle(BaseContract.IPresenter presenter) {
        getLifecycle().addObserver(presenter);
        return (V) this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mFragmentView = inflater.inflate(R.layout.base_layout, container, false);
            mRootView = findViewById(R.id.id_root_view);
            mStatusBarView = findViewById(R.id.id_statusbar_view);
            mHeaderParentView = findViewById(R.id.id_header_container);
            mContentParentView = findViewById(R.id.id_content_container);
            mLoadingDialog = createLoadingDialog();
            mLoadingView = createLoadingView();
            View headerView = getHeaderView();
            View contentView = getContentView();
            int headerLayoutId = getHeaderLayoutId();
            int contentLayoutId = getContentLayoutId();
            if (headerLayoutId > 0) {
                headerView = View.inflate(getActivity(), headerLayoutId, null);
            }
            if (contentLayoutId > 0) {
                contentView = View.inflate(getActivity(), contentLayoutId, null);
            }
            if (null != headerView) {//-1 :MATCH_PARENT  -2:WRAP_CONTENT
                mHeaderParentView.addView(headerView, -1, -2);
            }
            if (null != contentView) {//-1 :MATCH_PARENT  -1 :MATCH_PARENT
                mContentParentView.addView(contentView, -1, -1);
            }
        }
        ViewGroup mViewGroup = (ViewGroup) mFragmentView.getParent();
        if (null != mViewGroup) {
            mViewGroup.removeView(mFragmentView);
        }
        return mFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterEventBus();
        unbindButterKnife();
        destroyImmersionBar();
        hideLoadingDialog();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isOnViewCreated = true;
        /*   attachPresenters(createPresenter());*/
        registerEventBus(enableEventBus());
        bindButterKnife();
        initValue();
        initWidgetAndEvent();
        onLazyInit();
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createPresenters();
    }

    private void destroyImmersionBar() {
        if (null != mImmersionBar) {
            mImmersionBar.destroy();
        }
    }

    protected void initImmersionBar() {
        if (enableImmersionBar() && null != getActivity()) {
            if (null == mImmersionBar) {
                mImmersionBar = ImmersionBar.with(getActivity(), this);
            } else {
                mImmersionBar.reset();
            }
            mImmersionBar.init();
        }
    }

    protected boolean enableImmersionBar() {
        return true;
    }

    /**
     * 懒加载模式下
     * 此方法只会在fragment显示到屏幕时才会调用
     **/
    protected void lazyInitData() {
    }

    /**
     * 只加载一次数据
     */

    protected boolean onLazyLoadOnce() {
        return true;
    }

    protected void initData() {
    }

    /**
     * 1，ViewPager + Fragment  懒加载
     * ,2， hide，show framgent  懒加载
     */
    private void onLazyInit() {
        if (!isOnViewCreated || !isVisible) return;
        initImmersionBar();
        //因为fragment每次显示到屏幕上时，都会调用initData方法，isLoaded字段为了只调用一次initData方法
        if (isLoaded && onLazyLoadOnce()) return;
        isLoaded = true;
        lazyInitData();
    }

    protected void initWidgetAndEvent() {
    }

    protected void initValue() {
    }

    protected void bindButterKnife() {
    }

    protected void unbindButterKnife() {
    }

    protected boolean enableEventBus() {
        return true;
    }

    private void registerEventBus(boolean enable) {
        if (enable && !isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    private void unregisterEventBus() {
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected boolean isRegisterEventBus() {
        return EventBus.getDefault().isRegistered(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//, priority = 100
    public final void onEventCenter(EventCenter event) {
        if (null != event) {
            onEventCallback(event);
        }
    }

    /**
     * 根据code或data类型区分当前事件类型
     */
    protected void onEventCallback(EventCenter event) {
        // handle event
    }

    protected View getHeaderView() {
        return null;
    }

    protected int getHeaderLayoutId() {
        return 0;
    }

    protected View getContentView() {
        return null;
    }

    protected int getContentLayoutId() {
        return 0;
    }

    protected final <T extends View> T findViewById(@IdRes int res) {
        if (null != mFragmentView) {
            return mFragmentView.findViewById(res);
        } else {
            return null;
        }
    }

    protected Dialog createLoadingDialog() {
        return null;
    }

    protected View createLoadingView() {
        return null;
    }

    private boolean isShowingLoading() {
        return null != mLoadingView && mContentParentView.indexOfChild(mLoadingView) >= 0
                || null != mLoadingDialog && mLoadingDialog.isShowing();
    }

    @Override
    public void showLoadingDialog() {
        if (!isShowingLoading()) {
            if (null != mLoadingView) mContentParentView.addView(mLoadingView, -1, -1);
            else if (null != mLoadingDialog) mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (isShowingLoading()) {
            if (null != mLoadingView) mContentParentView.removeView(mLoadingView);
            else if (null != mLoadingDialog) mLoadingDialog.dismiss();
        }
    }

    /**
     * ViewPager + Fragment
     * 切换页面时会回调
     **/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible && !getUserVisibleHint()) {
            onFragmentPause();
        }
        isVisible = getUserVisibleHint();
        if (isVisible) {
            onFragmentResume();
            onLazyInit();
        }
    }

    /**
     * add多个fragment到同一个layout id的时，
     * 调用hide和show时回调
     **/
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isVisible && hidden) {
            onFragmentPause();
        }
        isVisible = !hidden;
        if (isVisible) {
            onFragmentResume();
            onLazyInit();
        }
    }

    /**
     * fragment可见时回调
     */
    protected void onFragmentResume() {
    }

    /**
     * fragment不可见时回调
     */
    protected void onFragmentPause() {
    }
}
