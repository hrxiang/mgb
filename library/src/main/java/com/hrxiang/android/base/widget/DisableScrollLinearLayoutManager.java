package com.hrxiang.android.base.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by xianghairui on 2018/8/31
 */
public class DisableScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean canScrollVertically;
    private boolean canScrollHorizontally;

    public DisableScrollLinearLayoutManager(Context context, boolean canScrollVertically,
                                            boolean canScrollHorizontally) {
        super(context);
        this.canScrollHorizontally = canScrollHorizontally;
        this.canScrollVertically = canScrollVertically;
    }

    public DisableScrollLinearLayoutManager(Context context, int orientation,
                                            boolean reverseLayout, boolean canScrollVertically,
                                            boolean canScrollHorizontally) {
        super(context, orientation, reverseLayout);
        this.canScrollHorizontally = canScrollHorizontally;
        this.canScrollVertically = canScrollVertically;
    }

    @Override
    public boolean canScrollVertically() {
        return canScrollVertically;
    }

    @Override
    public boolean canScrollHorizontally() {
        return canScrollHorizontally;
    }
}
