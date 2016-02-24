package com.khasang.vkphoto.util;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

public class StorageUtils {
    public static final String STORAGE_PATH = "storage_path";

    public static File getStorageDirectories(SharedPreferences sharedPreferences) {
        if (sharedPreferences.contains(STORAGE_PATH)) {
            File file = new File(sharedPreferences.getString(STORAGE_PATH, ""));
            if (file.exists()) {
                return file;
            }
        }

        final String state = Environment.getExternalStorageState();
        Logger.d("getExternalStorageState " + state);
        File mostFreeFile = null;
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_REMOVED.equals(state)) {  // we can read the External Storage...
            //Retrieve the primary External Storage:
            final File primaryExternalStorage = Environment.getExternalStorageDirectory();

            //Retrieve the External Storages root directory:
            final String externalStorageRootDir;
            if ((externalStorageRootDir = primaryExternalStorage.getParent()) == null) {  // no parent...
                Logger.d("External Storage: " + primaryExternalStorage + "\n");
                mostFreeFile = primaryExternalStorage;
            } else {
                final File externalStorageRoot = new File(externalStorageRootDir);
                final File[] files = externalStorageRoot.listFiles();
                if (files != null) {
                    for (final File file : files) {
                        if (file.isDirectory() && file.canRead() && (file.listFiles().length > 0)) {  // it is a real directory (not a USB drive)...
                            Logger.d("External Storage: " + file.getAbsolutePath() + "\n");
                            Logger.d("free space " + file.getAbsolutePath() + " " + file.getFreeSpace());
                            if (mostFreeFile == null || mostFreeFile.getFreeSpace() < file.getFreeSpace()) {
                                mostFreeFile = file;
                            }
                        }
                    }
                } else {
                    mostFreeFile = primaryExternalStorage;
                }
            }
        }
        if (mostFreeFile != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(STORAGE_PATH, mostFreeFile.getAbsolutePath());
            editor.apply();
        }
        return mostFreeFile;
    }
}
