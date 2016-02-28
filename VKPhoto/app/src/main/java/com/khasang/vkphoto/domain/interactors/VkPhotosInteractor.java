package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.ArrayList;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VkPhotosInteractor {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);

    void addPhotos(ArrayList<String> listUploadedFiles, PhotoAlbum photoAlbum);
}
