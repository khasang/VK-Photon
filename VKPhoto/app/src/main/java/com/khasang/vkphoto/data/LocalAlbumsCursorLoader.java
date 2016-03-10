package com.khasang.vkphoto.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.util.ImageFileFilter;

import java.io.File;

public class LocalAlbumsCursorLoader extends android.support.v4.content.CursorLoader {
    private LocalAlbumSource localAlbumSource;

    public LocalAlbumsCursorLoader(Context context, LocalAlbumSource localAlbumSource) {
        super(context);
        this.localAlbumSource = localAlbumSource;
    }

    @Override
    public Cursor loadInBackground() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID,
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

        Cursor cursor = getContext().getContentResolver().query(
                images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        try {
            Log.i("ListingImages", " query count=" + cursor.getCount());
        } catch (NullPointerException e) {/*NOP*/}

        if (cursor.moveToFirst()) {
            String bucketID, bucketName, date, thumbPath;
            int bucketIDColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                // Get the field values
                bucketID = cursor.getString(bucketIDColumn);
                bucketName = cursor.getString(bucketNameColumn);
                date = cursor.getString(dateColumn);
                thumbPath = cursor.getString(dataColumn);
                String filePath = thumbPath.substring(0, thumbPath.lastIndexOf("/"));
                //TODO: убрать костыль ниже, выяснив, почему в cursor попадают пустые альбомы
                int photosCount = new File(filePath).listFiles(new ImageFileFilter()).length;
                if (photosCount > 0) {
                    builder = matrixCursor.newRow();
                    builder.add(bucketID)
                            .add(bucketName)
                            .add(filePath)
                            .add(thumbPath)
                            .add(photosCount);
                }
                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucketID
                        + "  bucketName=" + bucketName
                        + "  date_taken=" + date
                        + "  _data=" + thumbPath);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return matrixCursor;
    }
}
