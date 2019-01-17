package com.hrxiang.android.base.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by hairui.xiang on 2017/9/20.
 */

public class AppUtils {
    private static final String UNIQUE_ID = "uniqueID";

    private static Context getContext() {
        return ContextProvider.getContext();
    }

    /**
     * 调用系统分享
     */
    public static void toSystemShare(String shareTitle, String shareContent, String chooserTitle) {
        Intent intentItem = new Intent(Intent.ACTION_SEND);
        intentItem.setType("text/plain");
        intentItem.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        intentItem.putExtra(Intent.EXTRA_TEXT, shareContent);
        intentItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(Intent.createChooser(intentItem, chooserTitle));
    }

    /**
     * need < uses-permission android:name =“android.permission.GET_TASKS” />
     * 判断是否前台运行
     */
    public static boolean isRunningForeground() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != am) {
            List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
            if (taskList != null && !taskList.isEmpty()) {
                ComponentName componentName = taskList.get(0).topActivity;
                return componentName != null && componentName.getPackageName().equals(getContext().getPackageName());
            }
        }
        return false;
    }

    /**
     * 获取App包 信息版本号
     */
    public PackageInfo getPackageInfo() {
        PackageManager packageManager = getContext().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 判断APK包是否已经安装
     *
     * @param packageName 包名
     * @return 包存在则返回true，否则返回false
     */
    public static boolean isPackageExists(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("Package name cannot be null or empty !");
        }
        try {
            ApplicationInfo info = getContext().getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return null != info;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * @return 当前程序的版本名称
     */
    public static String getVersionName() {
        String version = null;
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getContext().getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 方法: getVersionCode
     * 描述: 获取客户端版本号
     *
     * @return int    版本号
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getContext().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private static String getUniqueId() {
        return SpHelper.getObj(UNIQUE_ID, String.class, UUID.randomUUID().toString());
    }

    /**
     * 获取渠道，用于打包
     */
    public static String getMetaData(String metaName) {
        String result = null;
        try {
            ApplicationInfo appInfo = getContext().getPackageManager()
                    .getApplicationInfo(getContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                result = appInfo.metaData.getString(metaName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取网络类型
     */
    public static String getNetType() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // networkInfo.getDetailedState();//获取详细状态。
        // networkInfo.getExtraInfo();//获取附加信息。
        // networkInfo.getReason();//获取连接失败的原因。
        // networkInfo.getType();//获取网络类型(一般为移动或Wi-Fi)。
        // networkInfo.getTypeName();//获取网络类型名称(一般取值“WIFI”或“MOBILE”)。
        // networkInfo.isAvailable();//判断该网络是否可用。
        // networkInfo.isConnected();//判断是否已经连接。
        // networkInfo.isConnectedOrConnecting();//：判断是否已经连接或正在连接。
        // networkInfo.isFailover();//：判断是否连接失败。
        // networkInfo.isRoaming();//：判断是否漫游
        return networkInfo.getTypeName();
    }

    /**
     * 获取设备制造商名称.
     *
     * @return 设备制造商名称
     */
    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备名称.
     *
     * @return 设备名称
     */
    public static String getModelName() {
        return Build.MODEL;
    }

    /**
     * 获取产品名称.
     *
     * @return 产品名称
     */
    public static String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * 获取品牌名称.
     *
     * @return 品牌名称
     */
    public static String getBrandName() {
        return Build.BRAND;
    }

    /**
     * 获取操作系统版本号.
     *
     * @return 操作系统版本号
     */
    public static int getOSVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取操作系统版本名.
     *
     * @return 操作系统版本名
     */
    public static String getOSVersionName() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取操作系统版本显示名.
     *
     * @return 操作系统版本显示名
     */
    public static String getOSVersionDisplayName() {
        return Build.DISPLAY;
    }

    /**
     * 获取主机地址.
     *
     * @return 主机地址
     */
    public static String getHost() {
        return Build.HOST;
    }

    public static String getCpu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Arrays.toString(Build.SUPPORTED_ABIS);
        } else {
            return Build.CPU_ABI;
        }
    }

    /**
     * apk安装
     */

    public static void install(File file) {
        if (file != null) {
            //apk文件的本地路径
            //会根据用户的数据类型打开android系统相应的Activity。
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProviderUtils.getUriForFile(file);
                //权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.parse("file://" + file.getAbsolutePath());
            }
            //设置intent的数据类型是应用程序application
            intent.setDataAndType(uri, "application/vnd.android.package-archive");//
            //为这个新apk开启一个新的activity栈
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //开始安装
            getContext().startActivity(intent);
            //关闭旧版本的应用程序的进程
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public static void install(Uri uri) {
        if (null != uri) {
            //会根据用户的数据类型打开android系统相应的Activity。
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if ("file".equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProviderUtils.getUriForFile(new File(uri.getPath()));
                //权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            //设置intent的数据类型是应用程序application
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            //为这个新apk开启一个新的activity栈
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //开始安装
            getContext().startActivity(intent);
            //关闭旧版本的应用程序的进程
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 获取当前进程名
     */
    public static String getProcessName() {
        String processName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != manager) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }

    /**
     * 获取进程名字
     */
    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != am) {
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (null != runningApps) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
                    if (processInfo.pid == pid) {
                        return processInfo.processName;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName2(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 包名判断是否为主进程
     */
    public static boolean isMainProcess() {
//        return context.getPackageName().equals(getProcessName(android.os.Process.myPid()));
        return getContext().getPackageName().equals(getProcessName());
    }

    public static boolean isDebug() {
        ApplicationInfo info = getContext().getApplicationInfo();
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
