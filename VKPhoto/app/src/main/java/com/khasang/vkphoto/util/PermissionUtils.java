package com.khasang.vkphoto.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class PermissionUtils {
    /** Id to identify a camera permission request. */
    private static final int REQUEST_STORAGE = 0;

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Logger.d("Storage Permission is granted");
                return true;
            } else {
                Logger.d("Storage Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                activity.finish();
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Logger.d("Storage Permission is granted");
            return true;
        }
    }
}
