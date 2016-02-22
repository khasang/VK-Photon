package com.khasang.vkphoto.presentation.presenter;

public interface VKPhotosPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);
}
