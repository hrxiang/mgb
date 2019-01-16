package com.hrxiang.android.base.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;


/**
 * Created by hairui.xiang on 2017/9/14.
 */

public class ThreadCountCalculator {
    private static final int DEFAULT_THREAD_COUNT = 3;
    private static final String CPU_NAME_REGEX = "cpu[0-9]+";
    private static final String CPU_LOCATION = "/sys/devices/system/cpu/";

    public static int calculateBestThreadCount() {
        return Math.min(calculateBestThreadCountByNetwork(), calculateBestThreadCountByCpuCount());
    }

    public static int calculateBestThreadCountByNetwork() {
        NetworkInfo info = NetworkUtils.getNetworkInfo();
        if (null == info || !info.isConnectedOrConnecting()) {
            return DEFAULT_THREAD_COUNT;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
//                break;
                return 4;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE://4g
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
//                        break;
                        return 3;
                    case TelephonyManager.NETWORK_TYPE_UMTS://3g
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
//                        break;
                        return 2;
                    case TelephonyManager.NETWORK_TYPE_GPRS://2g
                    case TelephonyManager.NETWORK_TYPE_EDGE:
//                        break;
                        return 1;
                    default:
//                        break;
                        return DEFAULT_THREAD_COUNT;
                }
//                break;
            default:
//                break;
                return DEFAULT_THREAD_COUNT;
        }
    }

    public static int calculateBestThreadCountByCpuCount() {
        File[] cpus = null;
        try {
            File cpuInfo = new File(CPU_LOCATION);
            final Pattern cpuNamePattern = Pattern.compile(CPU_NAME_REGEX);
            cpus = cpuInfo.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return cpuNamePattern.matcher(s).matches();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cpuCount = cpus != null ? cpus.length : 0;
        int availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors());
        return Math.min(DEFAULT_THREAD_COUNT, Math.max(availableProcessors, cpuCount));
    }
}
