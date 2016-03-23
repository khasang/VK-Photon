package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.domain.tasks.DownloadPhotoCallable;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.AlbumsPresenterImpl;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.JsonUtils;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Реализация интерфейса исполнителя запросов к службе синхронизации.
 * Создается внутри AlbumsPresenterImpl
 *
 * @see VkAlbumsInteractor
 * @see AlbumsPresenterImpl
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
     * Когда служба получит альбомы от ВК, вызовет колбэк метод getVKAlbumsEvent
     *
     * @see SyncService
     * @see SyncServiceProvider
     */
    @Override
    public void getAllVKAlbums() {
        if (checkSyncService()) {
            syncService.getAllVKAlbums();
        }
    }

    @Override
    public void addAlbum(final String title, final String description,
                         final int privacy, final int commentPrivacy) {
        checkSyncService();
        syncService.addAlbum(title, description, privacy, commentPrivacy);
    }

    @Override
    public void editAlbum(int albumId, String title, String description) {
        if (checkSyncService()) {
                syncService.editAlbum(albumId, title, description);
        }
    }

    @Override
    public void editPrivacyAlbum(int albumId, int privacy) {
        if (checkSyncService()) {
            syncService.editPrivacyAlbum(albumId, privacy);
        }
    }

    @Override
    public void deleteVkAlbum(MultiSelector multiSelector, Cursor cursor) {
        if (checkSyncService()) {
            List<Integer> selectedPositions = multiSelector.getSelectedPositions();
            List<PhotoAlbum> photoAlbumList = new ArrayList<>();
            if (cursor != null) {
                for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                    Integer position = selectedPositions.get(i);
                    cursor.moveToPosition(position);
                    photoAlbumList.add(new PhotoAlbum(cursor));
                }
                syncService.deleteSelectedVkPhotoAlbums(photoAlbumList);
            }
        }
    }

    @Override
    public void cancelAlbumsSync(List<PhotoAlbum> selectedAlbums) {
        if (checkSyncService()) {
            syncService.cancelAlbumsSync(selectedAlbums);
        }
    }

    boolean checkSyncService() {
        for (int i = 0; i < 4; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (setSyncService() && VKAccessToken.currentToken() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public File downloadAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor) {
        final File[] files = new File[1];
        RequestMaker.getPhotoById(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    final Photo photo = JsonUtils.getPhotos(response.json, Photo.class).get(0);
                    Future<File> fileFuture = executor.submit(new DownloadPhotoCallable(localPhotoSource, photo, photoAlbum));
                    files[0] = fileFuture.get();
                } catch (Exception e) {
                    sendError(ErrorUtils.JSON_PARSE_FAILED);
                }
            }
        }, photoAlbum.thumb_id);
        return files[0];
    }
}
      