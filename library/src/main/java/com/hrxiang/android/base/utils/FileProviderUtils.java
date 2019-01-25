package com.hrxiang.android.base.utils;

import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;


/**
 * Created by hairui.xiang on 2017/9/20.
 */

public class FileProviderUtils {
    private static final String FILE_PROVIDER_AUTHORITIES_VALUE = "%s.fileProvider";

    public static Uri getUriForFile(File file) {
        String author = String.format(FILE_PROVIDER_AUTHORITIES_VALUE, ContextProvider.getContext().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(ContextProvider.getContext(), author, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
