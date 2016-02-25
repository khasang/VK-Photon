package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class VkPhotosInteractorImpl implements VkPhotosInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public VkPhotosInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
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

    @Override
    public void deletePhotoById(List<Integer> selectedPositions, List<Photo> photoList) {
        if (checkSyncService()) {
            List<Photo> deletePhotoList = new ArrayList<>();
            if (photoList != null) {
                for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                    Integer position = selectedPositions.get(i);
                    deletePhotoList.add(photoList.get(position));
                }
                syncService.deleteVkPhotoById(deletePhotoList);
            }

        }
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
