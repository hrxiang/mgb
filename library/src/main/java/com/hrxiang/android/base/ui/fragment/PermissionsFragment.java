package com.hrxiang.android.base.ui.fragment;

import android.support.annotation.NonNull;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

/**
 * Created by xianghairui on 2018/12/13.
 */
public class PermissionsFragment extends RxSupportFragment implements EasyPermissions.PermissionCallbacks {

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
