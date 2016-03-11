package com.khasang.vkphoto.util;

import android.util.Log;

/**
 * Created by aleksandrlihovidov on 07.02.16.
 */
public class Logger {
    public static final String TAG = "VKphotoLog";
    private static boolean isDebug = true;

    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

}
