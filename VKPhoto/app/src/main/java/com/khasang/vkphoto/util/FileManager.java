package com.khasang.vkphoto.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.File;

public class FileManager {
    public static final String BASE_DIR_NAME = "VK Photo";

    public static boolean initBaseDirectory(Context context) {
        File baseDirectory = StorageUtils.getStorageDirectories(PreferenceManager.getDefaultSharedPreferences(context));
        if (baseDirectory == null) {
            return false;
        }
        File vkPhotoDirectory = new File(baseDirectory.getAbsolutePath() + "/" + BASE_DIR_NAME);
        return !(!vkPhotoDirectory.exists() || vkPhotoDirectory.isFile()) || vkPhotoDirectory.mkdir();
    }
}
