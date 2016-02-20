package com.khasang.vkphoto.database.tables;

import com.khasang.vkphoto.database.MySqliteHelper;

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
    public static final String FILE_PATH = "filepath";
    public static final String SYNC_STATUS = "sync_status";

    public static final String FIELDS = MySqliteHelper.PRIMARY_KEY
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
            + SYNC_STATUS + " integer";
}
