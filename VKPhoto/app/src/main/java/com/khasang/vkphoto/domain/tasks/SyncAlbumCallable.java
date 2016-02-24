package com.khasang.vkphoto.domain.tasks;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.data.vk.VKPhotoSource;
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

import java.util.List;
import java.util.concurrent.Callable;

public class SyncAlbumCallable implements Callable<Boolean> {
    private LocalPhotoSource localPhotoSource;
    private PhotoAlbum photoAlbum;

    public SyncAlbumCallable(PhotoAlbum photoAlbum, LocalPhotoSource localPhotoSource) {
        this.localPhotoSource = localPhotoSource;
        this.photoAlbum = photoAlbum;
    }

    @Override
    public Boolean call() throws Exception {
        Logger.d("Entered SyncAlbumCallable " + photoAlbum.title + " call");
        VKPhotoSource vkPhotoSource = new VKPhotoSource();
        vkPhotoSource.getPhotosByAlbumId(photoAlbum.id);
        localPhotoSource.getPhotosByAlbumId(photoAlbum.id);
        VKRequest vkRequest = RequestMaker.getVkPhotosByAlbumIdRequest(photoAlbum.id);
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final List<Photo> photoList;
                try {
                    photoList = JsonUtils.getItems(response.json, Photo.class);
                    Logger.d("Got VKPhoto for photoAlbum " + photoAlbum.title);
                    localPhotoSource.getPhotosByAlbumId(photoAlbum.id);
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
        });


        return false;
    }

    private void sendError(String s) {
        EventBus.getDefault().postSticky(new ErrorEvent(s));
    }

}
