package com.khasang.vkphoto.domain.listeners;

import com.khasang.vkphoto.ui.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.List;

public class OnGetAllAlbumsListenerImpl implements OnGetAllAlbumsListener {
    private VkAlbumsView vkAlbumsView;

    public OnGetAllAlbumsListenerImpl(VkAlbumsView vkAlbumsView) {
        this.vkAlbumsView = vkAlbumsView;
    }

    @Override
    public void onSuccess(List<VKApiPhotoAlbum> photoAlbums) {
        vkAlbumsView.displayVkAlbums(photoAlbums);
    }

    @Override
    public void onVKError(VKError e) {
        Logger.d(e.errorMessage);
        vkAlbumsView.showConnectionError();
    }

    @Override
    public void onSyncServiceError() {
        vkAlbumsView.showSyncServiceError();
    }
}
