package com.khasang.vkphoto.database.tables;

import com.khasang.vkphoto.database.MySqliteHelper;

public class PhotosTable {
    public static final String TABLE_NAME = "photos";

    public static final String ALBUM_ID = "album_id";
    public static final String OWNER_ID = "owner_id";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String FILE_PATH = "filepath";
    public static final String SYNC_STATUS = "sync_status";

    public static final String FIELDS = MySqliteHelper.PRIMARY_KEY
            + ALBUM_ID + " integer, "
            + OWNER_ID + " integer, "
            + WIDTH + " integer, "
            + HEIGHT + " integer, "
            + TEXT + " text, "
            + DATE + " integer, "
            + LIKES + " integer, "
            + COMMENTS + " integer, "
            + FILE_PATH + " text, "
            + SYNC_STATUS + " integer";
}
