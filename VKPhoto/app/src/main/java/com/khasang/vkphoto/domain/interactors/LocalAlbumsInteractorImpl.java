package com.khasang.vkphoto.domain.interactors;

import android.content.Context;
import android.database.Cursor;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TAU on 07.03.2016.
 */
public class LocalAlbumsInteractorImpl implements LocalAlbumsInteractor {
    LocalAlbumSource localAlbumSource;

    public LocalAlbumsInteractorImpl(Context context) {
        localAlbumSource = new LocalAlbumSource(context);
    }

    @Override
    public void syncLocalAlbums(MultiSelector multiSelector, Cursor cursor) {
        Logger.d("user wants to syncLocalAlbums");
        Logger.d("no body");
    }

    @Override
    public List<PhotoAlbum> getAllLocalAlbums() {
        Logger.d("user wants to getAllLocalAlbums");
        return localAlbumSource.getAllLocalAlbums();
    }

    @Override
    public void addAlbum(String title, String description, int privacy, int commentPrivacy) {
        Logger.d("user wants to addAlbum");
        Logger.d("no body");
    }

    @Override
    public void deleteSelectedLocalAlbums(MultiSelector multiSelector, Cursor cursor) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        List<PhotoAlbum> deleteList = new ArrayList<>();
        if (cursor != null) {
            for (Integer position : selectedPositions) {
                cursor.moveToPosition(position);
                PhotoAlbum deleteAlbum = new PhotoAlbum(cursor);
                deleteList.add(deleteAlbum);
            }
            localAlbumSource.deleteLocalAlbums(deleteList);
        }
    }
}
