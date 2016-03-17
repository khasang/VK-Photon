package com.khasang.vkphoto.presentation.presenter.album;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.Presenter;

import java.util.List;

public interface AlbumPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deleteSelectedPhotos(MultiSelector multiSelector);

    void selectPhoto(MultiSelector multiSelector, final AppCompatActivity activity);

    void checkActionModeFinish(MultiSelector multiSelector);

    void hideActionModeItem(MultiSelector multiSelector, MenuItem menuItem);

    void addPhotos(List<Photo> photosList, PhotoAlbum photoAlbum);
}
