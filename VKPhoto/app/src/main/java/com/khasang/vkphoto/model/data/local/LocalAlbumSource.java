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
import com.khasang.vkphoto.model.events.LocalAlbumCreatedEvent;
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
            EventBus.getDefault().postSticky(new LocalAlbumCreatedEvent());
        }
    }

    public void updateAlbum(PhotoAlbum photoAlbum) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        db.close();
    }


    public void deleteAlbum() {

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
        db.close();
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
