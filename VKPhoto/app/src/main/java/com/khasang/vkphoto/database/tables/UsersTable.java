package com.khasang.vkphoto.database.tables;

import com.khasang.vkphoto.database.MySqliteHelper;

public class UsersTable {
    public static final String TABLE_NAME = "users";

    public final static String FIELD_PHOTO_50 = "photo_50";
    public final static String FIRST_NAME = "first_name";
    public final static String LAST_NAME = "last_name";

    public static final String FIELDS = MySqliteHelper.PRIMARY_KEY
            + FIELD_PHOTO_50 + " text, "
            + FIRST_NAME + " text, "
            + LAST_NAME + " text";
}
