package com.khasang.vkphoto.domain.listeners;

import com.khasang.vkphoto.services.SyncService;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.List;

/**
 * Интерфейс колбэка для SyncService.
 * MainPresenter -> MainInteractor-> в SyncService
 * При получении альбомов/ошибки от ВК, служба вызывает колбэк методы.
 *
 * @see SyncService#getAllAlbums(OnGetAllAlbumsListener)
 * @see com.khasang.vkphoto.ui.presenter.MainPresenterImpl
 */
public interface OnGetAllAlbumsListener {
    void onSuccess(List<VKApiPhotoAlbum> photoAlbums);

    void onVKError(VKError e);

    void onSyncServiceError();
}
      