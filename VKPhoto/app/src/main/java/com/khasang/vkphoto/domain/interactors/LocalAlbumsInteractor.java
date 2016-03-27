package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

/**
 * Created by TAU on 07.03.2016.
 */
public interface LocalAlbumsInteractor {
    void syncLocalAlbums(MultiSelector multiSelector, Cursor cursor);
    List<PhotoAlbum> getAllLocalAlbums();
    void addAlbum(String title);
    void deleteLocalAlbums(MultiSelector multiSelector, Cursor cursor);
    void editLocalOrSyncAlbum(PhotoAlbum photoAlbum, String newTitle);
}
