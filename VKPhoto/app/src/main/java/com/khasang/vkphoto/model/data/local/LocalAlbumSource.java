package com.khasang.vkphoto.model.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.khasang.vkphoto.database.MySQliteHelper;
import com.khasang.vkphoto.model.data.interfaces.AlbumSource;

public class LocalAlbumSource implements AlbumSource {
    private SQLiteDatabase db;
    private MySQliteHelper dbHelper;

    public LocalAlbumSource(Context context) {
        this.dbHelper = MySQliteHelper.getInstance(context);
    }

    @Override
    public void saveAlbum() {

    }

    @Override
    public void updateAlbum() {

    }

    @Override
    public void deleteAlbum() {

    }

    @Override
    public void deleteAlbums() {

    }

    @Override
    public void getAlbumById() {

    }

    @Override
    public void getAllAlbums() {

    }
}
