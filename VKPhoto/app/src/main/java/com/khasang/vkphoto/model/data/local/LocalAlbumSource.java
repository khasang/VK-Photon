package com.khasang.vkphoto.model.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.khasang.vkphoto.database.MySqliteHelper;
import com.khasang.vkphoto.model.data.interfaces.AlbumSource;

public class LocalAlbumSource implements AlbumSource {
    private SQLiteDatabase database;
    private MySqliteHelper dbHelper;

    public LocalAlbumSource(Context context) {
        this.dbHelper = MySqliteHelper.getInstance(context);
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
