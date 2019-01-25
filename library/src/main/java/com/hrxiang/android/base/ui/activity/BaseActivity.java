package com.hrxiang.android.base.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.hrxiang.android.R;
import com.hrxiang.android.base.presenter.contract.BaseContract;
import com.hrxiang.android.base.utils.ActivityStack;
import com.hrxiang.android.base.utils.EventBus.EventCenter;
import com.hrxiang.android.base.utils.LanguageHelper;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class BaseActivity
        /* <V extends BaseContract.IView, Presenter extends BasePresenter>*/
        extends PermissionsActivity implements BaseContract.IView {

    /* protected Presenter mPresenter;*/
    private Dialog mLoadingDialog;
    private View mLoadingView;
    private InputMethodManager mInputMethodManager;
    protected View mRootView;
    protected View mStatusBarView;
    private FrameLayout mHeaderParentView;
    private FrameLayout mContentParentView;
    protected ImmersionBar mImmersionBar;

    /**
     * 8.0以上app内 语言切换失效bug
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.attachBaseContext(newBase));
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.pop(this);
        unregisterEventBus();
        unbindButterKnife();
        destroyImmersionBar();
        hideLoadingDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
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
            headerView = View.inflate(this, headerLayoutId, null);
        }
        if (contentLayoutId > 0) {
            contentView = View.inflate(this, contentLayoutId, null);
        }
        if (null != headerView) {//-1 :MATCH_PARENT  -2:WRAP_CONTENT
            mHeaderParentView.addView(headerView, -1, -2);
        }
        if (null != contentView) {//-1 :MATCH_PARENT  -1 :MATCH_PARENT
            mContentParentView.addView(contentView, -1, -1);
        }

        ActivityStack.push(this);
        createPresenters();
        registerEventBus(enableEventBus());
        // 设置是否允许滑动退出
        setSwipeBackEnable(enableSwipeBack());
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        //getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        //getSwipeBackLayout().setEdgeSize(200);
        bindButterKnife();
        initValue();
        initWidgetAndEvent();
        initImmersionBar();
        initData();
    }

    protected void initValue() {
    }

    protected void initWidgetAndEvent() {
    }

    protected void initData() {
    }

    protected void bindButterKnife() {
    }

    protected void unbindButterKnife() {
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

    private void destroyImmersionBar() {
        if (null != mImmersionBar) {
            mImmersionBar.destroy();
        }
    }

    protected void initImmersionBar() {
        if (enableImmersionBar()) {
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.init();
        }
    }

    protected boolean enableImmersionBar() {
        return true;
    }

    protected boolean enableSwipeBack() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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

    public boolean isRegisterEventBus() {
        return EventBus.getDefault().isRegistered(this);
    }

    /**
     * POSTING, 该事件在哪个线程发布出来的，就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程。
     * 使用这个方法时，不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。
     * <p>
     * MAIN, 不论事件是在哪个线程中发布出来的，都会在UI线程中执行，接收事件就会在UI线程中运行，所以在方法中是不能执行耗时操作的。
     * <p>
     * BACKGROUND, 如果事件是在UI线程中发布出来的，会在子线程中运行，如果事件本来就是子线程中发布出来的，那么函数直接在该子线程中执行。
     * <p>
     * ASYNC 无论事件在哪个线程发布，都会创建新的子线程在执行
     */
    @Subscribe(threadMode = ThreadMode.MAIN)//, priority = 100
    public final void onEventDispatchCenter(EventCenter event) {
        if (null != event) {
            onEventCallback(event);
        }
    }

    /**
     * 根据code或data类型区分当前事件类型
     */
    protected void onEventCallback(EventCenter event) {
    }

    @Override
    public <T> LifecycleTransformer<T> bindRxLifecycle() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public <V extends BaseContract.IView> V bindPresenterLifecycle(BaseContract.IPresenter presenter) {
        getLifecycle().addObserver(presenter);
        return (V) this;
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    protected void hideSoftKeyBoard() {
        View focusView = getCurrentFocus();
        if (null != focusView) {
            if (null == mInputMethodManager) {
                mInputMethodManager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            }
            mInputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 2);
        }
    }
}
