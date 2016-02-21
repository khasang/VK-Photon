package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;
import com.khasang.vkphoto.util.Constants;

import org.greenrobot.eventbus.EventBus;

/**
 * Реализация интерфейса исполнителя запросов к службе синхронизации.
 * Создается внутри VKAlbumsPresenterImpl
 *
 * @see VkAlbumsInteractor
 * @see VKAlbumsPresenterImpl
 * @see SyncServiceImpl
 */
public class VkAlbumsInteractorImpl implements VkAlbumsInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public VkAlbumsInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        setSyncService();
    }

    /**
     * Получить ссылку на службу из SyncProvider syncService
     *
     * @see SyncService
     * @see SyncServiceProvider
     */
    private boolean setSyncService() {
        syncService = syncServiceProvider.getSyncService();
        return syncService != null;
    }

    @Override
    public void saveAlbum() {
        checkSyncService();
        syncService.saveAlbum();
    }

    /**
     * Отправить запрос в службу синхронизации на получение списка альбомов.
     * Когда служба получит альбомы от ВК, вызовет колбэк метод у onGetAllAlbumsListener
     *
     * @see SyncService
     * @see SyncServiceProvider
     */
    @Override
    public void getAllAlbums() {
        if (checkSyncService()) syncService.getAllAlbums();
    }

    boolean checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(Constants.SYNC_SERVICE_ERROR));
                return false;
            }
        }
        return true;
    }
}
      