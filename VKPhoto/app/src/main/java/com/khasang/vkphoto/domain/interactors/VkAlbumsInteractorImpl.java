package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;
import com.vk.sdk.VKAccessToken;

import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void syncAlbums(MultiSelector multiSelector, Cursor cursor) {
        if (checkSyncService()) {
            List<Integer> selectedPositions = multiSelector.getSelectedPositions();
            List<PhotoAlbum> photoAlbumList = new ArrayList<>();
            if (cursor != null) {
                for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                    Integer position = selectedPositions.get(i);
                    cursor.moveToPosition(position);
                    photoAlbumList.add(new PhotoAlbum(cursor));
                }
                syncService.syncAlbums(photoAlbumList);
            }
        }
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
        AsyncExecutor asyncExecutor = AsyncExecutor.create();
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                if (checkSyncService()) syncService.getAllAlbums();
            }
        });
    }

    @Override
    public void deleteVkAlbum(MultiSelector multiSelector, Cursor cursor) {
        if (checkSyncService()) {
            List<Integer> selectedPositions = multiSelector.getSelectedPositions();
            if (selectedPositions.size() == 1) {
                cursor.moveToPosition(selectedPositions.get(0));
                PhotoAlbum photoAlbum = new PhotoAlbum(cursor);
                syncService.deleteVKAlbumById(photoAlbum.getId());
            }
        }
    }

    boolean checkSyncService() {
        for (int i = 0; i < 4; i++) {
            if (setSyncService() && VKAccessToken.currentToken() != null) {
                return true;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
      