package com.khasang.vkphoto.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

public class StorageUtils {
    public static final String STORAGE_PATH = "storage_path";

    public static File getStorageDirectories(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(STORAGE_PATH)) {
            File file = new File(sharedPreferences.getString(STORAGE_PATH, ""));
            if (file.exists()) {
                return file;
            }
        }
        File mostFreeFile = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        if (mostFreeFile != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(STORAGE_PATH, mostFreeFile.getAbsolutePath());
            editor.apply();
        }
        return mostFreeFile;
    }
}
