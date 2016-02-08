package com.khasang.vkphoto.domain.interfaces;

import com.khasang.vkphoto.model.PhotoAlbum;
import com.vk.sdk.api.VKError;

import java.util.List;

public interface OnGetAllAlbumsListener {
    void onSuccess(List<PhotoAlbum> photoAlbums);

    void onVKError(VKError e);

    void onSyncServiceError();
}
      