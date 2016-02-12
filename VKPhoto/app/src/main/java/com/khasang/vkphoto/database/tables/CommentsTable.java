package com.khasang.vkphoto.database.tables;

import com.khasang.vkphoto.database.MySQliteHelper;

public class CommentsTable {
    public static final String TABLE_NAME = "comments";

    public static final String FROM_ID = "from_id";
    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String REPLY_TO_USER = "reply_to_user";
    public static final String REPLY_TO_COMMENT = "reply_to_comment";
    public static final String LIKES = "likes";
    public static final String PHOTO_ID = "photo_id";

    public static final String FIELDS = MySQliteHelper.PRIMARY_KEY
            + FROM_ID + " integer, "
            + DATE + " integer, "
            + TEXT + " text, "
            + REPLY_TO_USER + " integer, "
            + REPLY_TO_COMMENT + " integer, "
            + LIKES + " integer, "
            + PHOTO_ID + " integer";
}
