package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.Vector;

/** Created by bugtsa on 19-Feb-16. */
public interface VkAddAlbumPresenter extends Presenter {
    void addAlbum(final String title, final String description,
                  final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy);

    void onStart();

    void onStop();

    void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);
}

