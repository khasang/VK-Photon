package com.khasang.vkphoto.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

public class LocalPhotoSource {
    private Context context;
    private MySQliteHelper dbHelper;

    public LocalPhotoSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQliteHelper.getInstance(context);
    }

    public File savePhotoToAlbum(Photo photo, PhotoAlbum photoAlbum) {
        File imageFile = FileManager.saveImage(photo.getUrlToMaxPhoto(), photoAlbum);
        if (imageFile == null) {
            EventBus.getDefault().postSticky(new ErrorEvent("Photo " + photo.id + " wasn't saved"));
        } else {
            photo.filePath = imageFile.getAbsolutePath();
            if (getPhotoById(photo.id) == null) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert(PhotosTable.TABLE_NAME, null, PhotosTable.getContentValues(photo));
            } else {
                Logger.d("Photo " + photo.id + " exists");
                updatePhoto(photo);
            }
        }
        return imageFile;
    }


    public File getPhotoFile(Photo photo, PhotoAlbum photoAlbum) {
        File file = getLocalPhoto(photo.id);
        return file != null ? file : savePhotoToAlbum(photo, photoAlbum);
    }

    public File getLocalPhoto(int photoId) {
        File file;
        Photo localPhoto = getPhotoById(photoId);
        if (localPhoto == null) return null;
        file = new File(localPhoto.filePath);
        return file.exists() ? file : null;
    }

    public void savePhotos() {

    }

    public void updatePhoto(Photo photo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = PhotosTable.getContentValuesUpdated(photo, getPhotoById(photo.id));
        if (contentValues.size() > 0) {
            db.update(PhotosTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(photo.id)});
        }
    }

    public void deletePhoto() {

    }

    public void deletePhotos() {

    }

    public Photo getPhotoById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Photo photo = null;
        Cursor cursor = db.query(PhotosTable.TABLE_NAME, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            photo = new Photo(cursor);
        }
        cursor.close();
        return photo;
    }

    public void getPhotosByAlbumId() {

    }

    public void getAllPhotos() {

    }

    public List<File> getPhotosByAlbum(PhotoAlbum photoAlbum) {
        return null;
    }
}
