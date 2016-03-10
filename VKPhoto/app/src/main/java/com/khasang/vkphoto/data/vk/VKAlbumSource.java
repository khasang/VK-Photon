package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetAlbumEvent;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class VKAlbumSource {

    /**
     * Добавляет альбом на сервер VK с заданными параметрами
     * Создаёт (Обновляет) локальный альбом на девайсе
     * @param title
     * @param description
     * @param privacy
     * @param commentPrivacy
     * @param localAlbumSource
     */
    public void addAlbum(final String title, final String description,
                                 final int privacy, final int commentPrivacy,
                                 final LocalAlbumSource localAlbumSource) {
        RequestMaker.createEmptyAlbum(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final PhotoAlbum photoAlbum;
                try {
                    photoAlbum = JsonUtils.getPhotoAlbum(response.json);
                    Logger.d("Create Album successfully");
                    localAlbumSource.updateAlbum(photoAlbum);
                    EventBus.getDefault().post(new GetAlbumEvent(photoAlbum));
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
        }, title, description, privacy, commentPrivacy);
    }

    public void updateAlbum() {

    }

    public void deleteAlbumById(int albumId) {
        RequestMaker.deleteVkAlbumById(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Logger.d("Delete VKPhotoAlbum successfully");
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
