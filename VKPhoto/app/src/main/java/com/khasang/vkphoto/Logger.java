package com.khasang.vkphoto;

import android.util.Log;

/**
 * Created by aleksandrlihovidov on 07.02.16.
 */
public class Logger {
    public static final String TAG = "LOG";
    private static boolean isDebug = true;

    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

}
