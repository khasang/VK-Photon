package com.khasang.vkphoto.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
    public static boolean isNetworkOnline(Context context) {
        return checkNetworkConnected(getActiveNetworkInfo(context));
    }

    private static boolean checkNetworkConnected(NetworkInfo netInfo) {
        try {
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            Logger.d(e.toString());
        }
        return false;
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static int getNetworkType(Context context) {
        NetworkInfo netInfo = getActiveNetworkInfo(context);
        if (checkNetworkConnected(netInfo)) {
        return netInfo.getType();
        }
        return -1;
    }

}
