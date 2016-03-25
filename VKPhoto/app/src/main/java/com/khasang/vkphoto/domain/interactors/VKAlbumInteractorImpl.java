package com.khasang.vkphoto.domain.interactors;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.util.ErrorUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class VKAlbumInteractorImpl implements VKAlbumInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public VKAlbumInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        setSyncService();
    }

    private boolean setSyncService() {
        syncService = syncServiceProvider.getSyncService();
        return syncService != null;
    }

    @Override
    public void getPhotosByAlbumId(int albumId) {
        if (checkSyncService()) {
            syncService.getVKPhotosByAlbumId(albumId);
        }
    }

    @Override
    public void deleteSelectedVkPhotos(MultiSelector multiSelector, List<Photo> photoList) {
        if (checkSyncService()) {
            List<Integer> selectedPositions = multiSelector.getSelectedPositions();
            List<Photo> deletePhotoList = new ArrayList<>();
            if (photoList != null) {
                for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                    Integer position = selectedPositions.get(i);
                    deletePhotoList.add(photoList.get(position));
                }
                syncService.deleteSelectedVkPhotos(deletePhotoList);
            }
        }
    }

    @Override
    public void getAllLocalAlbums() {
        if (checkSyncService()) syncService.getAllLocalAlbumsList();
    }

    boolean checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(ErrorUtils.SERVICE_CONNECTING_ERROR));
                return false;
            }
        }
        return true;
    }

}
