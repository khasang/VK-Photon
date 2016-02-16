package com.khasang.vkphoto.ui.view;

import com.khasang.vkphoto.model.PhotoAlbum;

public interface VkAlbumsView {
    void displayVkSaveAlbum(PhotoAlbum photoAlbum);

    void displayVkAlbums();

    void showError(String s);
}
      