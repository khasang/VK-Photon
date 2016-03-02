package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public interface VKAlbumPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deleteSelectedVkPhotos(MultiSelector multiSelector);

    void selectPhoto(MultiSelector multiSelector, final AppCompatActivity activity);

    void checkActionModeFinish(MultiSelector multiSelector, Context context);

    void addPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum);
}
