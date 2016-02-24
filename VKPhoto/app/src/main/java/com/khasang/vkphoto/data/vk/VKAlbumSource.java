package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.domain.events.GetVkSaveAlbumEvent;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPrivacy;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class VKAlbumSource {

    public void createEmptyAlbum() {
        RequestMaker.createEmptyAlbum(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete (VKResponse response) {
                super.onComplete(response);
                final PhotoAlbum photoAlbum;
                try {
                    photoAlbum = JsonUtils.getJsonTest(response.json);
                    Logger.d("Create Album successfully");
                    EventBus.getDefault().postSticky(new GetVkSaveAlbumEvent(photoAlbum));
                } catch (Exception e) {
                    sendError(e.toString());
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendError(error.toString());
            }

            void sendError(String s) {
                EventBus.getDefault().postSticky(new ErrorEvent(s));
            }
        }, "Test 16.02", "test album", VKPrivacy.PRIVACY_FRIENDS , VKPrivacy.PRIVACY_NOBODY);
    }

    public void updateAlbum() {

    }

    public void deleteAlbumBuId(int albumId) {
        RequestMaker.deleteVkAlbumById(new VKRequest.VKRequestListener() {
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
        }, albumId);
    }

    public void deleteAlbums() {

    }

    public void getAllAlbums() {
        RequestMaker.getAllVkAlbums(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final List<PhotoAlbum> photoAlbumList;
                try {
                    photoAlbumList = JsonUtils.getItems(response.json, PhotoAlbum.class);
                    Logger.d("Got VKAlbums successfully");
                    EventBus.getDefault().postSticky(new GetVKAlbumsEvent(photoAlbumList));
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
    }

    void sendError(String s) {
        EventBus.getDefault().postSticky(new ErrorEvent(s));
    }

}
