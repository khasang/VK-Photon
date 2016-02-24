package com.khasang.vkphoto.presentation.view;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/** Created by bugtsa on 19-Feb-16. */
public interface VkAddAlbumView {
    void displayVkAddAlbum(PhotoAlbum photoAlbum);

    void showError(String s);
}
