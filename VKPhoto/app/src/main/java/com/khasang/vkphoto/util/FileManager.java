package com.khasang.vkphoto.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

public class FileManager {
    public static final String BASE_DIR_NAME = "VK Photo";
    public static final String BASE_DIRECTORY = "base_directory";

    public static String createAlbumDirectory(String albumName, Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        File baseDirectory = getBaseDirectory(defaultSharedPreferences);
        if (baseDirectory == null) {
            initBaseDirectory(context);
            baseDirectory = getBaseDirectory(defaultSharedPreferences);
        }
        assert baseDirectory != null;
        File albumDirectory = new File(baseDirectory.getAbsolutePath() + "/" + albumName);
        if (checkDirectoryExists(albumDirectory) || albumDirectory.mkdirs()) {
            return albumDirectory.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static boolean deleteAlbumDirectory(String path) {
        File albumDirectory = new File(path);
        return checkDirectoryExists(albumDirectory) && deleteDir(albumDirectory);
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public static boolean initBaseDirectory(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        File storageDirectory = getStorageDirectory(defaultSharedPreferences);
        if (storageDirectory == null) {
            Logger.d("storageDirectory = null");
            return false;
        }
        File baseDirectory = new File(storageDirectory.getAbsolutePath() + "/" + BASE_DIR_NAME);
        if (checkDirectoryExists(baseDirectory) || baseDirectory.mkdirs()) {
            if (!defaultSharedPreferences.getString(BASE_DIRECTORY, "").equals(baseDirectory.getAbsolutePath())) {
                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                editor.putString(BASE_DIRECTORY, baseDirectory.getAbsolutePath());
                editor.apply();
            }
            return true;
        }
        return false;
    }

    private static File getStorageDirectory(SharedPreferences sharedPreferences) {
        return StorageUtils.getStorageDirectories(sharedPreferences);
    }

    private static File getBaseDirectory(SharedPreferences sharedPreferences) {
        String basePath = sharedPreferences.getString(BASE_DIRECTORY, "");
        if (basePath.equals("")) {
            return null;
        }
        File baseDirectory = new File(basePath);
        return checkDirectoryExists(baseDirectory) ? baseDirectory : null;
    }

    private static boolean checkDirectoryExists(File file) {
        return file.exists() && file.isDirectory();
    }
}
