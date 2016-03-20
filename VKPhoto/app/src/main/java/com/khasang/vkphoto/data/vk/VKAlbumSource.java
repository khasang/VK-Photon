package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.domain.events.VKAlbumEvent;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class VKAlbumSource {

    /**
     * Добавляет альбом на сервер VK с заданными параметрами
     * Создаёт (Обновляет) локальный альбом на девайсе
     *
     * @param title
     * @param description
     * @param privacy
     * @param commentPrivacy
     * @param localAlbumSource
     */
    public void addAlbum(final String title, final String description,
                         final int privacy, final int commentPrivacy,
                         final LocalAlbumSource localAlbumSource) {
        RequestMaker.createEmptyAlbum(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final PhotoAlbum photoAlbum;
                try {
                    photoAlbum = JsonUtils.getPhotoAlbum(response.json);
                    Logger.d("Create Album successfully");
                    localAlbumSource.updateAlbum(photoAlbum, false);
                    EventBus.getDefault().postSticky(new VKAlbumEvent());
                } catch (Exception e) {
                    sendError(ErrorUtils.JSON_PARSE_FAILED);
                }
            }

        }, title, description, privacy, commentPrivacy);
    }

    public void updateAlbum() {

    }

    public void deleteAlbumById(int albumId) {
        RequestMaker.deleteVkAlbumById(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Logger.d("Delete VKPhotoAlbum successfully");
            }
        }, albumId);
    }

    public void deleteAlbums() {

    }

    public void getAllAlbums() {
        RequestMaker.getAllVkAlbums(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final List<PhotoAlbum> photoAlbumList;
                try {
                    photoAlbumList = JsonUtils.getItems(response.json, PhotoAlbum.class);
                    Logger.d("Got VKAlbums successfully");
                    EventBus.getDefault().postSticky(new GetVKAlbumsEvent(photoAlbumList));
                } catch (Exception e) {
                    sendError(ErrorUtils.JSON_PARSE_FAILED);
                }
            }

        });
    }

    public void editAlbumById(int albumId, String title, String description) {
        RequestMaker.editAlbumById(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Logger.d("Edit VKPhotoAlbum successfully");
            }
        }, albumId, title, description);
    }


}
