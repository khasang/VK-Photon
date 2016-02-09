package com.khasang.vkphoto.domain.listeners;

import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.vk.sdk.api.VKError;

import java.util.List;

/**
 * Интерфейс колбэка для SyncService.
 * MainPresenter -> MainInteractor-> в SyncService
 * При получении альбомов/ошибки от ВК, служба вызывает колбэк методы.
 *
 * @see com.khasang.vkphoto.services.SyncServiceImpl#getAllAlbums(OnGetAllAlbumsListener)
 * @see com.khasang.vkphoto.ui.presenter.MainPresenterImpl
 */
public interface OnGetAllAlbumsListener {
    void onSuccess(List<PhotoAlbum> photoAlbums);

    void onVKError(VKError e);

    void onSyncServiceError();
}
      