package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VKPhotosPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);
}
