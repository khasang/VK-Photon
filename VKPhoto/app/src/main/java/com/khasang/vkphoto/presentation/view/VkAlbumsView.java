package com.khasang.vkphoto.presentation.view;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public interface VkAlbumsView extends VkView {
    void displayVkSaveAlbum(PhotoAlbum photoAlbum);
    void displayAlbums();
    Cursor getAdapterCursor();
}
      