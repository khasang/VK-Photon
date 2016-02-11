package com.khasang.vkphoto.model.data;

public interface PhotoSource {
    void savePhoto();

    void savePhotos();

    void updatePhoto();

    void deletePhoto();

    void deletePhotos();

    void getPhotoById();

    void getPhotosByAlbumId();

    void getAllPhotos();

}
