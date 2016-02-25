package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/** Created by bugtsa on 19-Feb-16. */
public interface VkAddAlbumPresenter extends Presenter {
    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);

    void onStart();

    void onStop();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);
}

