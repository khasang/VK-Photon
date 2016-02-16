package com.khasang.vkphoto.ui.view;

import com.khasang.vkphoto.model.PhotoAlbum;

import java.util.List;

public interface VkAlbumsView {
    void displayVkSaveAlbum(PhotoAlbum photoAlbum);

    void displayVkAlbums(List<PhotoAlbum> photoAlbumList);

    void showError(String s);
}
      