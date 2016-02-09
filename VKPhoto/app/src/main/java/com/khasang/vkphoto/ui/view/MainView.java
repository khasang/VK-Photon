package com.khasang.vkphoto.ui.view;

import com.khasang.vkphoto.model.album.PhotoAlbum;

import java.util.List;

public interface MainView {

    void displayVkAlbums(List<PhotoAlbum> photoAlbumList);

    void displayGalleryAlbums();

    void showConnectionError();

    void showSyncServiceError();
}
      