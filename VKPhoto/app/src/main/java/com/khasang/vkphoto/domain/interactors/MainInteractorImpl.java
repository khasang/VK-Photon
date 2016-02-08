package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.interfaces.OnGetAllAlbumsListener;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.services.SyncService;

public class MainInteractorImpl implements MainInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public MainInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        syncService = syncServiceProvider.getSyncService();
    }

    @Override
    public void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener) {
        if (syncService == null) {
            syncService = syncServiceProvider.getSyncService();
        }
        if (syncService==null){
            onGetAllAlbumsListener.onSyncServiceError();
        }
        syncService.getAllAlbums(onGetAllAlbumsListener);
    }
}
      