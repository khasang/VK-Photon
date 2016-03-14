package com.khasang.vkphoto.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.ImageFileFilter;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Logger.d("update " + photoAlbum.id + " photoAlbum");
            ContentValues contentValues = PhotoAlbumsTable.getContentValuesUpdated(photoAlbum, oldAlbum);
            if (contentValues.size() > 0) {
                db.update(PhotoAlbumsTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                        new String[]{String.valueOf(photoAlbum.id)});
                EventBus.getDefault().postSticky(new LocalAlbumEvent());
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
                EventBus.getDefault().postSticky(new LocalAlbumEvent());
            } finally {
                db.endTransaction();
            }
        }
    }

    //метод не уничтожает папку. только все ФОТО в ней
    //после его использования необходимо заново выполнить поиск всего, что программа считает альбомом
    public void deleteLocalAlbums(List<PhotoAlbum> photoAlbumList) {
        for (PhotoAlbum photoAlbum: photoAlbumList) {
            Logger.d("now deleting file: " + photoAlbum.filePath);
            File dir = new File(photoAlbum.filePath);
            String[] children = dir.list();
            ImageFileFilter filter = new ImageFileFilter();
            for (String child : children) {
                File file = new File(dir, child);
                if (filter.accept(file))
                    if (!file.delete())
                        Logger.d("error while deleting file: " + photoAlbum.filePath);
            }
        }
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

    public List<PhotoAlbum> getAllLocalAlbums() {
        Set<String> imagePaths = new HashSet<>();
        List<PhotoAlbum> photoAlbumList = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.MediaColumns.DATA};
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
        Cursor cursor = context.getContentResolver().query(uri, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
        if (cursor != null) {
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                String string = cursor.getString(dataIndex);
                imagePaths.add(string);
            }
            cursor.close();

            for (String imagePath : imagePaths) {
                String albumPath = imagePath.substring(0, imagePath.lastIndexOf("/"));
                String title = albumPath.substring(albumPath.lastIndexOf("/") + 1);

                boolean isInTheList = false;
                for (PhotoAlbum photoAlbumListed: photoAlbumList)
                    if (photoAlbumListed.filePath.equals(albumPath)) {
                        isInTheList = true;
                        break;
                    }
                if (!isInTheList) {
                    PhotoAlbum photoAlbum = new PhotoAlbum(title, albumPath);
                    File dir = new File(albumPath);
                    File [] photosInDir = dir.listFiles();
//                    File [] photosInDir = dir.listFiles(new ImageFileFilter());
                    photoAlbum.size = photosInDir.length;
                    if (photoAlbum.size > 0) photoAlbumList.add(photoAlbum);
                }
            }
        }
        return photoAlbumList;
    }

    public List<String> getAllImagesPathes() {//находит и возвращает все фотографии на девайсе
        List<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(dataIndex);
                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    public void setSyncStatus(List<PhotoAlbum> photoAlbumList, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PhotoAlbumsTable.SYNC_STATUS, status);
            String[] whereArgs = new String[photoAlbumList.size()];
            for (int i = 0; i < photoAlbumList.size(); i++) {
                whereArgs[i] = String.valueOf(photoAlbumList.get(i).id);
            }
            db.update(PhotoAlbumsTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                    whereArgs);
            db.setTransactionSuccessful();
            EventBus.getDefault().postSticky(new LocalAlbumEvent());
        } finally {
            db.endTransaction();
        }

    }
}
