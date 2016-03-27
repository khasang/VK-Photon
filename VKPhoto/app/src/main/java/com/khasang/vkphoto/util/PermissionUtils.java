package com.khasang.vkphoto.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Logger.d("Storage Permission is granted");
                return true;
            } else {
                Logger.d("Storage Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_EXTERNAL_STORAGE);
//                activity.finish();
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Logger.d("Storage Permission is granted");
            return true;
        }
    }
}
