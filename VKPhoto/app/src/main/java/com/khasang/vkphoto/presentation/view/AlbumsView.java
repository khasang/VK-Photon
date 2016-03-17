package com.khasang.vkphoto.presentation.view;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface AlbumsView extends View {
    void confirmSync(final MultiSelector multiSelector);

    void displayVkSaveAlbum(PhotoAlbum photoAlbum);

    void displayAlbums();

    void displayRefresh(boolean refreshing);

    Cursor getAdapterCursor();
}
      