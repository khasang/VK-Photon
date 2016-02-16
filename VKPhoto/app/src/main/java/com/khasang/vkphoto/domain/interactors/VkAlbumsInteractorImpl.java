package com.khasang.vkphoto.domain.interactors;

import android.content.Context;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.model.events.ErrorEvent;
import com.khasang.vkphoto.services.SyncService;
import com.khasang.vkphoto.ui.presenter.VKAlbumsPresenterImpl;

import org.greenrobot.eventbus.EventBus;

/**
 * Реализация интерфейса исполнителя запросов к службе синхронизации.
 * Создается внутри VKAlbumsPresenterImpl
 *
 * @see VkAlbumsInteractor
 * @see VKAlbumsPresenterImpl
 * @see com.khasang.vkphoto.services.SyncServiceImpl
 */
public class VkAlbumsInteractorImpl implements VkAlbumsInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;
    private Context context;

    public VkAlbumsInteractorImpl(SyncServiceProvider syncServiceProvider, Context context) {
        this.syncServiceProvider = syncServiceProvider;
        this.context = context.getApplicationContext();
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
        checkSyncService();
        syncService.getAllAlbums();
    }

    void checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(context.getString(R.string.sync_service_error)));
            }
        }
    }
}
      