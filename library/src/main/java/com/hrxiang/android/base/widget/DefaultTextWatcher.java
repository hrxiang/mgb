package com.hrxiang.android.base.widget;

import android.text.TextWatcher;

/**
 * Created by xianghairui on 2018/9/5
 */
public interface DefaultTextWatcher extends TextWatcher {
    @Override
    default void beforeTextChanged(CharSequence s, int start, int count,
                                   int after) {

    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
