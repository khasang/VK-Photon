package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.File;

public class VKPhotoSource {

    public void savePhotoToAlbum(File file, PhotoAlbum photoAlbum) {
        if (file.exists()) {
            RequestMaker.uploadPhoto(file, photoAlbum, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Logger.d(response.responseString);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                }
            });
        }
    }

    public void savePhotos() {

    }

    public void updatePhoto() {

    }

    public void deletePhoto() {

    }

    public void deletePhotos() {

    }

    public void getPhotoById() {

    }

    public void getPhotosByAlbumId() {

    }

    public void getAllPhotos() {

    }
}
