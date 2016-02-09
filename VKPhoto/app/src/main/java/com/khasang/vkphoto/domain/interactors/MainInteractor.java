package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;

/**
 * Интерфейс исполнителя запросов к службе синхронизации.
 * Создается внутри MainPresenterImpl
 * @see com.khasang.vkphoto.ui.presenter.MainPresenterImpl
 * @see com.khasang.vkphoto.services.SyncServiceImpl
 */
//Todo расширить интерфейс необходимыми методами
public interface MainInteractor {
    void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener);
}
      