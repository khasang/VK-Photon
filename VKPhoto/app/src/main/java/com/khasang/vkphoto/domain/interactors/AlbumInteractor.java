package com.khasang.vkphoto.domain.interactors;


import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public interface AlbumInteractor {
    void getPhotosByAlbumId(int albumId);

    void getAllLocalAlbums();

    void deleteSelectedVkPhotos(MultiSelector multiSelector, List<Photo> photoList);

    void syncPhotos(MultiSelector multiSelector, ArrayList<Photo> photos, PhotoAlbum photoAlbum);
}
