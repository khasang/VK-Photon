package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VKAlbumsPresenter extends Presenter {
    void saveAlbum();

    void getAllAlbums();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);

}
      