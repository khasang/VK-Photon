package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;

/**
 * Интерфейс исполнителя запросов к службе синхронизации.
 * Создается внутри VKAlbumsPresenterImpl
 * @see VKAlbumsPresenterImpl
 * @see SyncServiceImpl
 */
//Todo расширить интерфейс необходимыми методами
public interface VkAlbumsInteractor {
    void saveAlbum();

    void getAllAlbums();
}
      