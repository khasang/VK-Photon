package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VKAlbumsPresenter extends Presenter {
    void syncAlbums(MultiSelector multiSelector);

    void getAllAlbums();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);

    void deleteVkAlbums(MultiSelector multiSelector);
}
      