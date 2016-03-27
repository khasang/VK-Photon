package com.khasang.vkphoto.presentation.view;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public interface AlbumView extends View {
    void displayPhotos(List<Photo> photos);

    void displayAllLocalAlbums(List<PhotoAlbum> albumsList);

    void displayRefresh(boolean refreshing);

    List<Photo> getPhotoList();

    void removePhotosFromView();
}
