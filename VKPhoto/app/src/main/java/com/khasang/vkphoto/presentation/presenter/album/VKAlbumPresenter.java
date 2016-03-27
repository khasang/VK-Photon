package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VKAlbumPresenter extends AlbumPresenter {

    void getAllLocalAlbums();

    void goToPhotoAlbum(Context context, PhotoAlbum selectedLocalPhotoAlbum, PhotoAlbum vkAlbum);
}
