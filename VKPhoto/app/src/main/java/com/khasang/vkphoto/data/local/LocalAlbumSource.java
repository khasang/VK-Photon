package com.khasang.vkphoto.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.khasang.vkphoto.data.database.MySQliteHelper;
import com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.VKAlbumEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.ImageFileFilter;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalAlbumSource {
    private Context context;
    private MySQliteHelper dbHelper;

    public LocalAlbumSource(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = MySQliteHelper.getInstance(context);
    }

    public void saveAlbum(VKApiPhotoAlbum apiPhotoAlbum, boolean sendEvent) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String path = FileManager.createAlbumDirectory(apiPhotoAlbum.id + "", context);
        if (path == null) {
            EventBus.getDefault().postSticky(new ErrorEvent(ErrorUtils.ALBUM_NOT_CREATED_ERROR));
        } else {
            PhotoAlbum photoAlbum = new PhotoAlbum(apiPhotoAlbum);
            photoAlbum.filePath = path;
            db.insert(PhotoAlbumsTable.TABLE_NAME, null, PhotoAlbumsTable.getContentValues(photoAlbum));
            if (sendEvent) {
                EventBus.getDefault().postSticky(new VKAlbumEvent());
            }
        }
    }

    public void updateAlbum(PhotoAlbum photoAlbum, boolean isLocal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        PhotoAlbum oldAlbum = getAlbumById(photoAlbum.id);
        if (oldAlbum == null) {
            saveAlbum(photoAlbum, false);
        } else {
            Logger.d("update " + photoAlbum.id + " photoAlbum");
            ContentValues contentValues = PhotoAlbumsTable.getContentValuesUpdated(photoAlbum, oldAlbum, isLocal);
            if (contentValues.size() > 0) {
                db.update(PhotoAlbumsTable.TABLE_NAME, contentValues, BaseColumns._ID + " = ?",
                        new String[]{String.valueOf(photoAlbum.id)});
                EventBus.getDefault().postSticky(new VKAlbumEvent());
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
                EventBus.getDefault().postSticky(new VKAlbumEvent());
            } finally {
                db.endTransaction();
            }
        }
    }

    //метод не уничтожает папку. только все ФОТО в ней
    //после его использования необходимо заново выполнить поиск всего, что программа считает альбомом
    public void deleteLocalAlbums(List<PhotoAlbum> photoAlbumList, LocalPhotoSource localPhotoSource) {
        for (PhotoAlbum photoAlbum : photoAlbumList) {
            Logger.d("now deleting photoAlbum: " + photoAlbum.filePath);
            List<Photo> deleteList = localPhotoSource.getLocalPhotosByAlbumId(photoAlbum.id);
            localPhotoSource.deleteLocalPhotos(deleteList);
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

    public Cursor getAllLocalAlbums() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                BaseColumns._ID,
                PhotoAlbumsTable.TITLE,
                PhotoAlbumsTable.FILE_PATH,
                PhotoAlbumsTable.THUMB_FILE_PATH,
                PhotoAlbumsTable.SIZE});
        MatrixCursor.RowBuilder builder;
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};
        // We want to order the albums by reverse chronological order. We abuse the
        // "WHERE" parameter to insert a "GROUP BY" clause into the SQL statement.
        // The template for "WHERE" parameter is like:
        //    SELECT ... FROM ... WHERE (%s)
        // and we make it look like:
        //    SELECT ... FROM ... WHERE (1) GROUP BY 1,(2)
        // The "(1)" means true. The "1,(2)" means the first two columns specified
        // after SELECT. Note that because there is a ")" in the template, we use
        // "(2" to match it.
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

        Cursor cursor = context.getContentResolver().query(
                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        try {
            Logger.d("ListingPhotoAlbums" + " query count=" + cursor.getCount());
        } catch (NullPointerException e) {/*NOP*/
            Logger.d("Null exception cursor");
        }

        if (cursor.moveToFirst()) {
            String id, title, thumbPath;
            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int thumbPathColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                id = cursor.getString(idColumn);
                title = cursor.getString(titleColumn);
                thumbPath = cursor.getString(thumbPathColumn);
                String filePath = thumbPath.substring(0, thumbPath.lastIndexOf("/"));
                File[] files = new File(filePath).listFiles(new ImageFileFilter());
                builder = matrixCursor.newRow();
                builder.add(id)
                        .add(title)
                        .add(filePath)
                        .add(thumbPath);
                if (files != null) {
                    int photosCount = files.length;
                    builder.add(photosCount);
                } else {
                    builder.add(0);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return matrixCursor;
    }

    public List<PhotoAlbum> getAllLocalAlbumsList() {
        List<PhotoAlbum> albumsList = new ArrayList<>();
        Cursor cursor = getAllLocalAlbums();
        if (cursor.moveToFirst()) {
            do {
                albumsList.add(new PhotoAlbum(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
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
            EventBus.getDefault().postSticky(new VKAlbumEvent());
        } finally {
            db.endTransaction();
        }
    }

    public List<PhotoAlbum> getAlbumsToSync() {
        List<PhotoAlbum> photoAlbumList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String statuses = Constants.SYNC_FAILED + ", " + Constants.SYNC_STARTED + ", " + Constants.SYNC_SUCCESS;
        Cursor cursor = db.query(PhotoAlbumsTable.TABLE_NAME, null, PhotoAlbumsTable.SYNC_STATUS + " in (" + statuses + ")", null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                photoAlbumList.add(new PhotoAlbum(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return photoAlbumList;
    }
}
