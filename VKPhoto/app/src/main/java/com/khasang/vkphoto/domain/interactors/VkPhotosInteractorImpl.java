package com.khasang.vkphoto.domain.interactors;

import android.content.Context;
import android.util.Log;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Anton on 21.02.2016.
 */
public class VkPhotosInteractorImpl implements VkPhotosInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;
    private Context context;

    public VkPhotosInteractorImpl(SyncServiceProvider syncServiceProvider, Context context) {
        this.syncServiceProvider = syncServiceProvider;
        this.context = context.getApplicationContext();
        setSyncService();
    }

    private boolean setSyncService() {
        syncService = syncServiceProvider.getSyncService();
        return syncService != null;
    }

    @Override
    public void getPhotosByAlbumId(int albumId) {
        if (checkSyncService()) syncService.getPhotosByAlbumId(albumId);
    }

    boolean checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(context.getString(R.string.sync_service_error)));
                return false;
            }
        }
        return true;
    }

}
