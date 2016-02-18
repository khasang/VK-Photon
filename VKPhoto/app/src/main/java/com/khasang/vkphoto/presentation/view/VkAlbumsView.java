package com.khasang.vkphoto.presentation.view;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VkAlbumsView {
    void displayVkSaveAlbum(PhotoAlbum photoAlbum);

    void displayVkAlbums();

    void showError(String s);
}
      