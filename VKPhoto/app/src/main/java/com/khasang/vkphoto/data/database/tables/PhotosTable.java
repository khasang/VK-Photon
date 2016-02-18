package com.khasang.vkphoto.data.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.presentation.model.Photo;

public class PhotosTable {
    public static final String TABLE_NAME = "photos";

    public static final String ALBUM_ID = "album_id";
    public static final String OWNER_ID = "owner_id";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String LIKES = "likes";
    public static final String CAN_COMMENT = "can_comment";
    public static final String COMMENTS = "comments";
    public static final String FILE_PATH = "filepath";

    public static final String SYNC_STATUS = "sync_status";
    public static final String FIELDS = MySQliteHelper.PRIMARY_KEY
            + ALBUM_ID + " integer, "
            + OWNER_ID + " integer, "
            + WIDTH + " integer, "
            + HEIGHT + " integer, "
            + TEXT + " text, "
            + DATE + " integer, "
            + LIKES + " integer, "
            + CAN_COMMENT + " integer, "
            + COMMENTS + " integer, "
            + FILE_PATH + " text, "
            + SYNC_STATUS + " integer";

    public static ContentValues getContentValues(Photo photo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BaseColumns._ID, photo.id);
        contentValues.put(ALBUM_ID, photo.album_id);
        contentValues.put(OWNER_ID, photo.owner_id);
        contentValues.put(WIDTH, photo.width);
        contentValues.put(HEIGHT, photo.height);
        contentValues.put(TEXT, photo.text);
        contentValues.put(DATE, photo.date);
        contentValues.put(LIKES, photo.likes);
        contentValues.put(CAN_COMMENT, photo.can_comment);
        contentValues.put(COMMENTS, photo.comments);
        contentValues.put(FILE_PATH, photo.filePath);
        contentValues.put(SYNC_STATUS, photo.syncStatus);
        return contentValues;
    }

    public static ContentValues getContentValuesUpdated(Photo newPhoto, Photo oldPhoto) {
        ContentValues contentValues = new ContentValues();
        if (oldPhoto.album_id != newPhoto.album_id) {
            contentValues.put(ALBUM_ID, newPhoto.album_id);
        }
        if (oldPhoto.owner_id != newPhoto.owner_id) {
            contentValues.put(OWNER_ID, newPhoto.owner_id);
        }
        if (oldPhoto.width != newPhoto.width) {
            contentValues.put(WIDTH, newPhoto.width);
        }
        if (oldPhoto.height != newPhoto.height) {
            contentValues.put(HEIGHT, newPhoto.height);
        }
        if (oldPhoto.owner_id != newPhoto.owner_id) {
            contentValues.put(OWNER_ID, newPhoto.owner_id);
        }
        if (!oldPhoto.text.equals(newPhoto.text)) {
            contentValues.put(TEXT, newPhoto.text);
        }
        if (oldPhoto.date != newPhoto.date) {
            contentValues.put(DATE, newPhoto.date);
        }
        if (oldPhoto.likes != newPhoto.likes) {
            contentValues.put(LIKES, newPhoto.likes);
        }
        if (oldPhoto.can_comment != newPhoto.can_comment) {
            contentValues.put(CAN_COMMENT, newPhoto.can_comment);
        }
        if (oldPhoto.comments != newPhoto.comments) {
            contentValues.put(COMMENTS, newPhoto.comments);
        }
        if (!TextUtils.isEmpty(newPhoto.filePath) && !oldPhoto.filePath.equals(newPhoto.filePath)) {
            contentValues.put(FILE_PATH, newPhoto.filePath);
        }
        if (oldPhoto.syncStatus != newPhoto.syncStatus) {
            contentValues.put(SYNC_STATUS, newPhoto.syncStatus);
        }
        return contentValues;
    }
}
