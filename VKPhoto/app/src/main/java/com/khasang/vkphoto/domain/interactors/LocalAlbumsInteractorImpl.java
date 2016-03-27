package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAU on 07.03.2016.
 */
public class LocalAlbumsInteractorImpl implements LocalAlbumsInteractor {
    private SyncServiceProvider syncServiceProvider;
    private SyncService syncService;

    public LocalAlbumsInteractorImpl(SyncServiceProvider syncServiceProvider) {
        this.syncServiceProvider = syncServiceProvider;
        setSyncService();
    }

    private boolean setSyncService() {
        syncService = syncServiceProvider.getSyncService();
        return syncService != null;
    }

    @Override
    public void syncLocalAlbums(MultiSelector multiSelector, Cursor cursor) {
        Logger.d("user wants to syncLocalAlbums");
        Logger.d("no body");
    }

    @Override
    public List<PhotoAlbum> getAllLocalAlbums() {
        Logger.d("user wants to getAllLocalAlbums");
        Logger.d("no body");
        return null;
    }

    @Override
    public void addAlbum(String title) {
        if (checkSyncService()) {
            syncService.createLocalAlbum(title);
        }
    }

    @Override
    public void deleteLocalAlbums(MultiSelector multiSelector, Cursor cursor) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        List<PhotoAlbum> deleteList = new ArrayList<>();
        if (cursor != null) {
            for (Integer position : selectedPositions) {
                cursor.moveToPosition(position);
                PhotoAlbum deleteAlbum = new PhotoAlbum(cursor);
                deleteList.add(deleteAlbum);
            }
            if (checkSyncService()) {
                syncService.deleteSelectedLocalPhotoAlbums(deleteList);
            }
        }
    }

    @Override
    public void editLocalOrSyncAlbum(PhotoAlbum photoAlbum, String newTitle) {
        if (checkSyncService()) {
            syncService.editLocalOrSyncAlbum(photoAlbum, newTitle);
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
