package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.Presenter;

import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public interface LocalAlbumPresenter extends Presenter {
    List<Photo> getPhotosByAlbum(PhotoAlbum photoAlbum, Context context);
    void deleteSelectedLocalPhotos(MultiSelector multiSelector);
    void selectPhoto(MultiSelector multiSelector, final AppCompatActivity activity);
    void checkActionModeFinish(MultiSelector multiSelector, Context context);
}
