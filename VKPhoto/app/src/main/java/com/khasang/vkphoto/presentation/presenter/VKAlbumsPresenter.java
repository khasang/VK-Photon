package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.concurrent.ExecutorService;

public interface VKAlbumsPresenter extends Presenter {
    void syncAlbums(MultiSelector multiSelector);

    void getAllAlbums();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);

    void selectAlbum(MultiSelector multiSelector, AppCompatActivity activity);

    void checkActionModeFinish(MultiSelector multiSelector);

    void deleteVkAlbums(MultiSelector multiSelector);

    void downloadAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor);
}
      