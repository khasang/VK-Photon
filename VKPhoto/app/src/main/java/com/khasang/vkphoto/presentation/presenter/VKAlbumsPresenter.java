package com.khasang.vkphoto.presentation.presenter;

public interface VKAlbumsPresenter extends Presenter {
    void saveAlbum();

    void getAllAlbums();

    void onStart();

    void onStop();
}
      