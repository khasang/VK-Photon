package com.khasang.vkphoto.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.domain.entities.PhotoAlbum;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class LocalAlbumSource {
    private Context context;
    private MySQliteHelper dbHelper;

    public LocalAlbumSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQliteHelper.getInstance(context);
    }

    public void saveAlbum(VKApiPhotoAlbum apiPhotoAlbum) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String path = FileManager.createAlbumDirectory(apiPhotoAlbum.id + "", context);
        if (path == null) {
            EventBus.getDefault().postSticky(new ErrorEvent(apiPhotoAlbum.title + " couldn't be created!"));
        } else {
            PhotoAlbum photoAlbum = new PhotoAlbum(apiPhotoAlbum);
            photoAlbum.filePath = path;
            db.insert(PhotoAlbumsTable.TABLE_NAME, null, PhotoAlbumsTable.getContentValues(photoAlbum));
            EventBus.getDefault().postSticky(new LocalAlbumEvent());
        }
    }

    public void updateAlbum(PhotoAlbum photoAlbum) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        PhotoAlbum oldAlbum = getAlbumById(photoAlbum.id);
        if (oldAlbum == null) {
            saveAlbum(photoAlbum);
        } else {
            Logger.d("photoAlbum " + photoAlbum.id + " exists");
            if (photoAlbum.updated != oldAlbum.updated) {
                ContentValues contentValues = PhotoAlbumsTable.getContentValuesUpdated(photoAlbum, oldAlbum);
                if (contentValues.size() > 0) {
                    db.update(PhotoAlbumsTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                            new String[]{String.valueOf(photoAlbum.id)});
                    EventBus.getDefault().postSticky(new LocalAlbumEvent());
                }
            }
        }
    }


    public void deleteAlbum(PhotoAlbum photoAlbum) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (getAlbumById(photoAlbum.id) != null && !TextUtils.isEmpty(photoAlbum.filePath)) {
            db.beginTransaction();
            try {
                String[] whereArgs = {String.valueOf(photoAlbum.id)};
                db.delete(PhotosTable.TABLE_NAME, PhotosTable.ALBUM_ID + " = ?", whereArgs);
                db.delete(PhotoAlbumsTable.TABLE_NAME, BaseColumns._ID + " = ?", whereArgs);
                FileManager.deleteAlbumDirectory(photoAlbum.filePath);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void deleteAlbums() {

    }

    public PhotoAlbum getAlbumById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        PhotoAlbum photoAlbum = null;
        Cursor cursor = db.query(PhotoAlbumsTable.TABLE_NAME, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            photoAlbum = new PhotoAlbum(cursor);
        }
        cursor.close();
        return photoAlbum;
    }

    public List<PhotoAlbum> getAllAlbums() {
        List<PhotoAlbum> photoAlbumList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PhotoAlbumsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            photoAlbumList.add(new PhotoAlbum(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return photoAlbumList;
    }

    public Cursor getAllAlbumsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(PhotoAlbumsTable.TABLE_NAME, null, null, null, null, null, null);
    }
}
