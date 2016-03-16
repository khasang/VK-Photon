package com.khasang.vkphoto.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalPhotosEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalPhotoSource {
    private MySQliteHelper dbHelper;
    private Context context;

    public LocalPhotoSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQliteHelper.getInstance(context.getApplicationContext());
    }

    public File savePhotoToAlbum(Photo photo, PhotoAlbum photoAlbum) {
        File imageFile = FileManager.saveImage(photo.getUrlToMaxPhoto(), photoAlbum, photo.id);
        if (imageFile == null) {
            EventBus.getDefault().postSticky(new ErrorEvent(ErrorUtils.PHOTO_NOT_SAVED_ERROR));
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
        File file = getLocalPhotoFile(photo.id);
        return file != null ? file : savePhotoToAlbum(photo, photoAlbum);
    }

    public File getLocalPhotoFile(int photoId) {
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

    public void deleteLocalPhotos(List<Photo> photoList) {
        for (Photo photo : photoList) {
            Logger.d("now deleting file: " + photo.filePath);
            File file = new File(photo.filePath);
            if (!file.delete())
                Logger.d("error while deleting file: " + photo.filePath);
        }
    }

    public Photo getPhotoById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Photo photo = null;
        Cursor cursor = db.query(PhotosTable.TABLE_NAME, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            photo = new Photo(cursor, false);
        }
        cursor.close();
        return photo;
    }

    public List<Photo> getPhotosByAlbumId(int albumId) {
        List<Photo> photos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PhotosTable.TABLE_NAME, null, PhotosTable.ALBUM_ID + " = ?", new String[]{String.valueOf(albumId)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            photos.add(new Photo(cursor, false));
            cursor.moveToNext();
        }
        cursor.close();
        return photos;
    }

    public List<Photo> getLocalPhotosByAlbumId(int albumId) {
        List<Photo> result = new ArrayList<>();
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] PROJECTION_BUCKET = {
                BaseColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = context.getContentResolver().query(
                images, PROJECTION_BUCKET,
                MediaStore.Images.ImageColumns.BUCKET_ID + " = ?",
                new String[]{String.valueOf(albumId)}, null);
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo(cursor, true);
                photo.printPhoto();
                result.add(photo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        EventBus.getDefault().postSticky(new GetLocalPhotosEvent(result));
        return result;
    }
}
