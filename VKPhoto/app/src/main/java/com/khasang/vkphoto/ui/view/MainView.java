package com.khasang.vkphoto.ui.view;

import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.List;

public interface MainView {

    void displayVkAlbums(List<VKApiPhotoAlbum> photoAlbumList);

    void displayGalleryAlbums();

    void showConnectionError();

    void showSyncServiceError();
}
      