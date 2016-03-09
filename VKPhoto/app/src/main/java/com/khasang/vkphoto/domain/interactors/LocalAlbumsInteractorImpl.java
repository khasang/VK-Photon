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
    public void deleteSelectedLocalAlbums(MultiSelector multiSelector, List<PhotoAlbum> albumsList) {
        Logger.d("user wants to deleteSelectedLocalAlbums");
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        List<PhotoAlbum> deleteList = new ArrayList<>();
//        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (Integer position : selectedPositions)
            deleteList.add(albumsList.get(position));
        localAlbumSource.deleteLocalAlbums(deleteList);

//        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
//        List<PhotoAlbum> deleteList = new ArrayList<>();
//        if (albumsList != null) {
//            for (int i = 0; i < selectedPositions.size(); i++) {
//                Integer position = selectedPositions.get(i);
//                deleteList.add(albumsList.get(position));
//            }
//            localAlbumSource.deleteLocalAlbums(deleteList);
//        }
    }
}
