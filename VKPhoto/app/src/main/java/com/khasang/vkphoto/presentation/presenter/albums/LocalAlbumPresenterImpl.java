package com.khasang.vkphoto.presentation.presenter.albums;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class LocalAlbumPresenterImpl extends AlbumsPresenterBase implements LocalAlbumPresenter {
    @Override
    public void addAlbum(String title, String thumbPath) {

    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {

    }

    @Override
    public File getAlbumThumb(LocalPhotoSource localPhotoSource, PhotoAlbum photoAlbum, ExecutorService executor) {
        return new File(photoAlbum.thumbFilePath);
    }

    @Override
    public void deleteAlbums(MultiSelector multiSelector) {

    }

    @Override
    public void selectAlbum(MultiSelector multiSelector, AppCompatActivity activity) {

    }

    @Override
    public void getAllAlbums() {

    }

    @Override
    public void exportAlbums(MultiSelector multiSelector) {

    }
}
