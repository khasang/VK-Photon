package com.khasang.vkphoto.model.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.khasang.vkphoto.database.MySQliteHelper;
import com.khasang.vkphoto.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.events.ErrorEvent;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class LocalAlbumSource {
    private Context context;
    private SQLiteDatabase db;
    private MySQliteHelper dbHelper;

    public LocalAlbumSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQliteHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }

    public void saveAlbum(VKApiPhotoAlbum apiPhotoAlbum) {
        String path = FileManager.createAlbumDirectory(apiPhotoAlbum.id + "", context);
        if (path == null) {
            EventBus.getDefault().postSticky(new ErrorEvent(apiPhotoAlbum.title + " couldn't be created!"));
        } else {
            PhotoAlbum photoAlbum = new PhotoAlbum(apiPhotoAlbum);
            photoAlbum.filePath = path;
            db.insert(PhotoAlbumsTable.TABLE_NAME, null, PhotoAlbumsTable.getContentValues(photoAlbum));
        }
    }

    public void updateAlbum(PhotoAlbum photoAlbum) {
        PhotoAlbum oldAlbum = getAlbumById(photoAlbum.id);
        if (oldAlbum == null) {
            saveAlbum(photoAlbum);
        } else {
            Logger.d("photoAlbum " + photoAlbum.id + " exists");
            ContentValues contentValues = PhotoAlbumsTable.getContentValuesUpdated(photoAlbum, oldAlbum);
            if (contentValues.size() > 0) {
                db.update(PhotoAlbumsTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                        new String[]{String.valueOf(photoAlbum.id)});
            }
        }
    }


    public void deleteAlbum() {

    }

    public void deleteAlbums() {

    }

    public PhotoAlbum getAlbumById(int id) {
        PhotoAlbum photoAlbum = null;
        Cursor cursor = db.query(PhotoAlbumsTable.TABLE_NAME, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            photoAlbum = PhotoAlbumsTable.getPhotoAlbum(cursor);
        }
        cursor.close();
        return photoAlbum;
    }

    public List<PhotoAlbum> getAllAlbums() {
        List<PhotoAlbum> photoAlbumList = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PhotoAlbumsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            photoAlbumList.add(PhotoAlbumsTable.getPhotoAlbum(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return photoAlbumList;
    }

    public Cursor getAllAlbumsCursor() {
        db = dbHelper.getReadableDatabase();
        return db.query(PhotoAlbumsTable.TABLE_NAME, null, null, null, null, null, null);
    }
}
