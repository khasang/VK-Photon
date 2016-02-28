package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public interface VKPhotosPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);

    void addPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum);
}
