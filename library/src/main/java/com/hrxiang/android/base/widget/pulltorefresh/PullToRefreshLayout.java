package com.hrxiang.android.base.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import in.srain.cube.views.ptr.PtrUIHandler;


/**
 * Created by hairui.xiang on 2017/8/4.
 */

public class PullToRefreshLayout extends Issues282PtrFrameLayout {

    public PullToRefreshLayout(Context context) {
        super(context);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // the following are default settings
        this.setResistance(1.7f);
        this.setRatioOfHeaderHeightToRefresh(1.1f);
        this.setDurationToClose(200);
        this.setDurationToCloseHeader(500);
        // default is false
        this.setPullToRefresh(false);
        // default is true
        this.setKeepHeaderWhenRefresh(true);
        // 这里初始化上面的头View：

//        ThreeColourDotsHeader mPullHeader = new ThreeColourDotsHeader(getContext());
        // 这里设置头View为上面自定义的头View：
//        this.setHeaderView(mPullHeader);
        // 下拉和刷新状态监听：
        // 因为ParallaxHeader已经实现过PtrUIHandler接口，所以直接设置为ParallaxHeader：
//        this.addPtrUIHandler(mPullHeader);

        // header
//        final MaterialHeader header = new MaterialHeader(getContext());
//        int[] colors = getResources().getIntArray(R.array.google_colors);
//        header.setColorSchemeColors(colors);
//        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
//        header.setPadding(0, DisplayUtils.dip2px(15), 0, DisplayUtils.dip2px(10));
//        this.setHeaderView(header);
//        this.addPtrUIHandler(header);
    }

    public void setPtrHeaderView(View header) {
        // 这里设置头View为上面自定义的头View：
        this.setHeaderView(header);
        // 下拉和刷新状态监听：
        if (header instanceof PtrUIHandler) {
            this.addPtrUIHandler((PtrUIHandler) header);
        }
    }
}
