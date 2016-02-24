package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Vector;


public class VKAlbumSource {

    private RequestMaker requestMaker = new RequestMaker();
    /**
     * Добавляет альбом на сервер VK с выбранными фотографиями на девайсе
     * @param title
     * @param description
     * @param privacy
     * @param comment_privacy
     */
    public void addAlbum(final String title, final String description,
                         final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy) {
        requestMaker.requestAddAlbum(title, description, listUploadedFiles, privacy, comment_privacy);
    }

    public void updateAlbum() {

    }

    public void deleteAlbumById(int albumId) {
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
