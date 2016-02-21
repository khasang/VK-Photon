package com.khasang.vkphoto.presentation.view;

import android.database.Cursor;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VkAlbumsView extends VkView {
    void displayVkSaveAlbum(PhotoAlbum photoAlbum);

    void displayVkAlbums();

    Cursor getAdapterCursor();
}
      