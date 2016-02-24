package com.khasang.vkphoto.domain.interactors;

import java.util.Vector;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VkPhotosInteractor {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);

    void addPhotos(Vector<String> listUploadedFiles);
}
