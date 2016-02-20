package com.khasang.vkphoto.services;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 19.02.2016.
 */
public class AllImagesService {

    public static List<String> listOfAllImages(Activity activity){
        List<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME };

        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index);
            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}
