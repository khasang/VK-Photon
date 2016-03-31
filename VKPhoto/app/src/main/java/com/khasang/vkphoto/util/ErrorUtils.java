package com.khasang.vkphoto.util;

import android.content.Context;
import android.util.SparseIntArray;

import com.khasang.vkphoto.R;

public class ErrorUtils {
    public static final int SERVICE_CONNECTING_ERROR = 1;
    public static final int JSON_PARSE_FAILED = 2;

    public static final int NO_INTERNET_CONNECTION_ERROR = 10;

    public static final int ALBUM_NOT_CREATED_ERROR = 20;

    public static final int PHOTO_NOT_SAVED_ERROR = 30;

    public static final int PHOTO_NOT_UPLOADED_ERROR = 40;

    public static final int VK_API_ERROR = -101;
    public static final int VK_CANCELED = -102;
    public static final int VK_REQUEST_NOT_PREPARED = -103;
    public static final int VK_JSON_FAILED = -104;
    public static final int VK_REQUEST_HTTP_FAILED = -105;

    private static SparseIntArray errorCodes = new SparseIntArray();

    static {
        errorCodes.put(SERVICE_CONNECTING_ERROR, R.string.service_connecting_error);
        errorCodes.put(NO_INTERNET_CONNECTION_ERROR, R.string.no_internet_connection_error);
        errorCodes.put(ALBUM_NOT_CREATED_ERROR, R.string.album_not_created_error);
        errorCodes.put(PHOTO_NOT_SAVED_ERROR, R.string.photo_not_saved_error);
        errorCodes.put(PHOTO_NOT_UPLOADED_ERROR, R.string.photo_not_uploaded_error);
    }

    public static String getErrorMessage(int errorCode, Context context) {
        int resId = errorCodes.get(errorCode);
        return resId == 0 ? null : context.getString(resId);
    }
}
