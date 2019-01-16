package com.hrxiang.android.net.api.bean;

import android.support.annotation.NonNull;
import com.hrxiang.android.net.utils.GsonFactory;

import java.io.Serializable;

/**
 * Created by xianghairui on 2018/12/25.
 */
public class BaseBean implements Serializable {
    @NonNull
    @Override
    public String toString() {
        return GsonFactory.create().toJson(this);
    }
}
