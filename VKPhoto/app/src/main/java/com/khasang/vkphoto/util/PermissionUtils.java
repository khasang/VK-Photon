package com.khasang.vkphoto.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {

    public static boolean isPermissionsGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED ) {
                Logger.d("Permissions is granted");
                return true;
            } else {
                Logger.d("Permissions is revoked");
                ActivityCompat.requestPermissions(activity, Constants.PERMISSIONS, Constants.REQUEST_PERMISSIONS);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Logger.d("Permissions is granted");
            return true;
        }
    }
}
