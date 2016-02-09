package com.khasang.vkphoto.domain.listeners;

import com.khasang.vkphoto.model.photo.Photo;
import com.vk.sdk.api.VKError;

import java.util.List;

public interface OnGetPhotosFromAlbumListener {
    void onSuccess(List<Photo> photoAlbums);

    void onVKError(VKError e);

    void onSyncServiceError();
}
