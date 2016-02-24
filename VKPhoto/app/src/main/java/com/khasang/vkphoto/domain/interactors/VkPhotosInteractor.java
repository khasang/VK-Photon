package com.khasang.vkphoto.domain.interactors;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VkPhotosInteractor {
    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);
}
