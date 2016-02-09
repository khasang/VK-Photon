package com.khasang.vkphoto.domain.interactors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.model.Items;
import com.khasang.vkphoto.model.Response;
import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.khasang.vkphoto.services.SyncService;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.lang.reflect.Type;

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
    private MainThread mainThread = new MainThreadImpl();

    public MainInteractorImpl(SyncServiceProvider syncServiceProvider) {
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
        syncService.getAllAlbums(getAllAlbumsRunnable(onGetAllAlbumsListener));
    }

    private Runnable getAllAlbumsRunnable(final OnGetAllAlbumsListener onGetAllAlbumsListener) {
        return new Runnable() {
            @Override
            public void run() {
                VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Type photoAlbumsType = new TypeToken<Response<Items<PhotoAlbum>>>() {
                        }.getType();
                        final Response<Items<PhotoAlbum>> albumsResponse = new Gson().fromJson(response.json.toString(), photoAlbumsType);
                        if (onGetAllAlbumsListener != null) {
                            mainThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetAllAlbumsListener.onSuccess(albumsResponse.response.results);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final VKError error) {
                        super.onError(error);
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetAllAlbumsListener.onVKError(error);
                            }
                        });
                    }
                });
            }
        };
    }
}
      