package com.khasang.vkphoto.domain.interactors;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalPhotosInteractorImpl implements LocalPhotosInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public LocalPhotosInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        setSyncService();
    }

    private boolean setSyncService() {
        syncService = syncServiceProvider.getSyncService();
        return syncService != null;
    }

    @Override
    public void getPhotosByAlbumId(int albumId) {
        if (checkSyncService()) syncService.getLocalPhotosByAlbumId(albumId);
    }

    @Override
    public void addLocalPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum) {
        Logger.d("user wants to addLocalPhotos");
        Logger.d("no body");
    }

    @Override
    public void deleteSelectedLocalPhotos(MultiSelector multiSelector, List<Photo> photoList) {
        Logger.d("user wants to deleteSelectedPhotos");
        if (checkSyncService()) {
            List<Integer> selectedPositions = multiSelector.getSelectedPositions();
            List<Photo> deletePhotoList = new ArrayList<>();
            if (photoList != null) {
                for (int i = 0; i < selectedPositions.size(); i++) {
                    Integer position = selectedPositions.get(i);
                    deletePhotoList.add(photoList.get(position));
                }
                syncService.deleteSelectedLocalPhotos(deletePhotoList);
            }
        }
    }

    boolean checkSyncService() {
        if (syncService == null) {
            if (!setSyncService()) {
                EventBus.getDefault().postSticky(new ErrorEvent(Constants.SYNC_FAILED));
                return false;
            }
        }
        return true;
    }
}
