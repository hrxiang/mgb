package com.hrxiang.android.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.hrxiang.android.net.HttpUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by xianghairui on 2019/1/17.
 */
public class IpGetUtils {

    public static String getIntranetIP() {
        ConnectivityManager manager = ((ConnectivityManager) ContextProvider.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if (null != manager) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                    try {
                        //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }

                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                    WifiManager wifiManager = (WifiManager) ContextProvider.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (null != wifiManager) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        return intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    public static String getOuterNetIP() {
        BufferedReader buff = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(30000);//读取超时
            urlConnection.setConnectTimeout(30000);//连接超时
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                InputStream is = urlConnection.getInputStream();
                buff = new BufferedReader(new InputStreamReader(is, "UTF-8"));//注意编码，会出现乱码
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = buff.readLine()) != null) {
                    builder.append(line);
                }

                buff.close();//内部会关闭 InputStream
                urlConnection.disconnect();

                //截取字符串
                int startIndex = builder.indexOf("{");//包含[
                int endIndex = builder.indexOf("}");//包含]
                String json = builder.substring(startIndex, endIndex + 1);//包含[startIndex,endIndex)
                JSONObject jo = new JSONObject(json);
                String ip = jo.getString("cip");
                return ip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
