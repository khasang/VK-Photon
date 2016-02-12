package com.khasang.vkphoto.database.tables;

import android.provider.BaseColumns;

import com.khasang.vkphoto.database.MySQliteHelper;

public class PhotoAlbumsTable {
    public static final String TABLE_NAME = "photoalbums";

    public static final String TITLE = "title";
    public static final String SIZE = "size";
    public static final String PRIVACY = "privacy";
    public static final String DESCRIPTION = "description";
    public static final String OWNER_ID = "owner_id";
    public static final String CAN_UPLOAD = "can_upload";
    public static final String UPDATED = "updated";
    public static final String THUMB_ID = "thumb_id";
    public static final String THUMB_SRC = "thumb_src";
    public static final String FILE_PATH = "filePath";
    public static final String SYNC_STATUS = "syncStatus";
    public static final String CREATE_PHOTOALBUMS_TABLE = String.format(MySQliteHelper.CREATE_TABLE, TABLE_NAME, BaseColumns._ID + MySQliteHelper.PRIMARY_KEY
            + TITLE + " text, "
            + SIZE + " integer, "
            + PRIVACY + " integer, "
            + DESCRIPTION + " text, "
            + OWNER_ID + " integer, "
            + CAN_UPLOAD + " integer, "
            + UPDATED + " integer, "
            + THUMB_ID + " integer, "
            + THUMB_SRC + " text, "
            + FILE_PATH + " text, "
            + SYNC_STATUS + " integer");
}
