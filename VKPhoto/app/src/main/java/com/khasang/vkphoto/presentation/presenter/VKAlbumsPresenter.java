package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public interface VKAlbumsPresenter extends Presenter {
    void saveAlbum();

    void getAllAlbums();

    void goToPhotoAlbum(PhotoAlbum photoAlbum);

}
      