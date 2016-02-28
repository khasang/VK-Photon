package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VKAlbumsPresenter extends Presenter {
    void syncAlbums(MultiSelector multiSelector);

    void getAllAlbums();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);

    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);

    void deleteVkAlbums(MultiSelector multiSelector);
}
      