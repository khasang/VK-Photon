package com.khasang.vkphoto.presentation.presenter;

import java.util.Vector;

public interface VKPhotosPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);

    void addPhotos(Vector<String> listUploadedFiles);
}
