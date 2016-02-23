package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.util.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.Vector;

/** Created by bugtsa on 19-Feb-16. */
public class VkAddAlbumInteractorImpl implements VkAddAlbumInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public VkAddAlbumInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        setSyncService();
    }

    @Override
    public void addAlbum(final String title, final String description,
                         final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy) {
        checkSyncService();
        syncService.addAlbum(title, description, listUploadedFiles, privacy, comment_privacy);
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

    void checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(Constants.SYNC_SERVICE_ERROR));
            }
        }
    }
}
