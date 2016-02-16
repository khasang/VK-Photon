package com.khasang.vkphoto.database.tables;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.khasang.vkphoto.database.MySQliteHelper;
import com.khasang.vkphoto.model.PhotoAlbum;

public class PhotoAlbumsTable {
    public static final String TABLE_NAME = "photoalbums";

    public static final String TITLE = "title";
    public static final String SIZE = "size";
    public static final String PRIVACY = "privacy";
    public static final String DESCRIPTION = "description";
    public static final String OWNER_ID = "owner_id";
    public static final String CAN_UPLOAD = "can_upload";
    public static final String UPDATED = "updated";
    public static final String CREATED = "created";
    public static final String THUMB_ID = "thumb_id";
    public static final String THUMB_SRC = "thumb_src";
    public static final String FILE_PATH = "filepath";
    public static final String SYNC_STATUS = "sync_status";

    public static final String FIELDS = MySQliteHelper.PRIMARY_KEY
            + TITLE + " text, "
            + SIZE + " integer, "
            + PRIVACY + " integer, "
            + DESCRIPTION + " text, "
            + OWNER_ID + " integer, "
            + CAN_UPLOAD + " integer, "
            + UPDATED + " integer, "
            + CREATED + " integer, "
            + THUMB_ID + " integer, "
            + THUMB_SRC + " text, "
            + FILE_PATH + " text, "
            + SYNC_STATUS + " integer";

    public static ContentValues getContentValues(PhotoAlbum photoAlbum) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BaseColumns._ID, photoAlbum.id);
        contentValues.put(TITLE, photoAlbum.title);
        contentValues.put(SIZE, photoAlbum.size);
        contentValues.put(PRIVACY, photoAlbum.privacy);
        contentValues.put(DESCRIPTION, photoAlbum.description);
        contentValues.put(OWNER_ID, photoAlbum.owner_id);
        contentValues.put(CAN_UPLOAD, photoAlbum.can_upload);
        contentValues.put(UPDATED, photoAlbum.updated);
        contentValues.put(CREATED, photoAlbum.created);
        contentValues.put(THUMB_ID, photoAlbum.thumb_id);
        contentValues.put(THUMB_SRC, photoAlbum.thumb_src);
        contentValues.put(FILE_PATH, photoAlbum.filePath);
        contentValues.put(SYNC_STATUS, photoAlbum.syncStatus);
        return contentValues;
    }

    public static ContentValues getContentValuesUpdated(PhotoAlbum newAlbum, PhotoAlbum oldAlbum) {
        ContentValues contentValues = new ContentValues();
        if (!oldAlbum.title.equals(newAlbum.title)) {
            contentValues.put(TITLE, newAlbum.title);
        }
        if (oldAlbum.size != newAlbum.size) {
            contentValues.put(SIZE, newAlbum.size);
        }
        if (oldAlbum.privacy != newAlbum.privacy) {
            contentValues.put(PRIVACY, newAlbum.privacy);
        }
        if (!oldAlbum.description.equals(newAlbum.description)) {
            contentValues.put(DESCRIPTION, newAlbum.description);
        }
        if (oldAlbum.owner_id != newAlbum.owner_id) {
            contentValues.put(OWNER_ID, newAlbum.owner_id);
        }
        if (oldAlbum.can_upload != newAlbum.can_upload) {
            contentValues.put(CAN_UPLOAD, newAlbum.can_upload);
        }
        if (oldAlbum.updated != newAlbum.updated) {
            contentValues.put(UPDATED, newAlbum.updated);
        }
        if (oldAlbum.created != newAlbum.created) {
            contentValues.put(CREATED, newAlbum.created);
        }
        if (oldAlbum.thumb_id != newAlbum.thumb_id) {
            contentValues.put(THUMB_ID, newAlbum.thumb_id);
        }
        if (!oldAlbum.thumb_src.equals(newAlbum.thumb_src)) {
            contentValues.put(THUMB_SRC, newAlbum.thumb_src);
        }
        if (!oldAlbum.filePath.equals(newAlbum.filePath)) {
            contentValues.put(FILE_PATH, newAlbum.filePath);
        }
        if (oldAlbum.syncStatus != newAlbum.syncStatus) {
            contentValues.put(SYNC_STATUS, newAlbum.syncStatus);
        }
        return contentValues;
    }
}
