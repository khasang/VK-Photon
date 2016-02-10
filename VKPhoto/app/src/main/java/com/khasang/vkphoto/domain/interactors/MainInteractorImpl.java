package com.khasang.vkphoto.domain.interactors;

import com.google.gson.Gson;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.services.SyncService;

/**
 * Реализация интерфейса исполнителя запросов к службе синхронизации.
 * Создается внутри MainPresenterImpl
 *
 * @see com.khasang.vkphoto.domain.interactors.MainInteractor
 * @see com.khasang.vkphoto.ui.presenter.MainPresenterImpl
 * @see com.khasang.vkphoto.services.SyncServiceImpl
 */
public class MainInteractorImpl implements MainInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;
    private MainThread mainThread;
    private Gson gson;

    public MainInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        mainThread = new MainThreadImpl();
        gson = new Gson();
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

    /**
     * Отправить запрос в службу синхронизации на получение списка альбомов.
     * Когда служба получит альбомы от ВК, вызовет колбэк метод у onGetAllAlbumsListener
     *
     * @see SyncService
     * @see SyncServiceProvider
     */
    @Override
    public void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener) {
        if (syncService == null) {
            if (!setSyncService()) {
                onGetAllAlbumsListener.onSyncServiceError();
                return;
            }
        }
        syncService.getAllAlbums(onGetAllAlbumsListener);
    }


}
      