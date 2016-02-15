package com.khasang.vkphoto.ui.view;

import com.khasang.vkphoto.model.PhotoAlbum;

import java.util.List;

public interface VkAlbumsView {

    void displayVkAlbums(List<PhotoAlbum> photoAlbumList);

    void showError(String s);
}
      