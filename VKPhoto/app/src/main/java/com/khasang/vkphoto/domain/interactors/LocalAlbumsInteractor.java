package com.khasang.vkphoto.domain.interactors;

import android.content.Context;
import android.database.Cursor;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by TAU on 07.03.2016.
 */
public interface LocalAlbumsInteractor {
    void syncLocalAlbums(MultiSelector multiSelector, Cursor cursor);
    List<PhotoAlbum> getAllLocalAlbums();
    void addAlbum(final String title, final String description, final int privacy, final int commentPrivacy);
    void deleteSelectedLocalAlbums(MultiSelector multiSelector, List<PhotoAlbum> albumsList);
}
