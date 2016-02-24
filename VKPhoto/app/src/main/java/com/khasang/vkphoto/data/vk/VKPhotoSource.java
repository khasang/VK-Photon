package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.Vector;

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

    public void savePhotos(Vector<String> listUploadedFiles, PhotoAlbum photoAlbum) {
        for (int ind = 0; ind < listUploadedFiles.size(); ind++){
            File file = new File(listUploadedFiles.get(ind));
            savePhotoToAlbum(file, photoAlbum);
        }
    }

    public void updatePhoto() {

    }

    public void deletePhoto(int photoId) {
        RequestMaker.deleteVkPhotoById(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Logger.d("Delete VKPhoto successfully");
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendError(error.toString());
            }
        }, photoId);
    }

    public void deletePhotos() {

    }

    public void getPhotoById() {

    }


    public void getPhotosByAlbumId(int albumId) {
        RequestMaker.getVkPhotosByAlbumId(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final List<Photo> photoList;
                try {
                    photoList = JsonUtils.getItems(response.json, Photo.class);
                    Logger.d("Got VKPhoto successfully");
                    EventBus.getDefault().postSticky(new GetVKPhotosEvent(photoList));
                } catch (Exception e) {
                    sendError(e.toString());
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendError(error.toString());
            }

        }, albumId);
    }


    void sendError(String s) {
        EventBus.getDefault().postSticky(new ErrorEvent(s));
    }
}
