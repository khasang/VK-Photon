package com.khasang.vkphoto.util;

import android.Manifest;

public class Constants {
    public static final int MAX_WIDTH = 1024;
    public static final int MAX_HEIGHT = 768;
    public static final int SYNC_NOT_STARTED = 0;
    public static final int SYNC_STARTED = 1;
    public static final int SYNC_SUCCESS = 2;
    public static final int SYNC_FAILED = 3;
    public static final int SYNC_DELETED = -1;

    public static final int NULL = -1;

    public static final int ALBUMS_SPAN_COUNT = 2;
    public static final int RECYCLERVIEW_SPACING = 10;
    //Errors

    public static final int START = 1;
    public static final int SEARCH = 2;
    public static final int ALBUM = 3;
    public static final int PHOTO = 4;

    public static final int REQUEST_PERMISSIONS = 0;

    public static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
}


