package com.khasang.vkphoto.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.executor.ThreadExecutor;
import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.Response;
import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.khasang.vkphoto.model.album.PhotoAlbums;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.lang.reflect.Type;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private ThreadExecutor executor = new ThreadExecutor();
    private MainThread mainThread = new MainThreadImpl();
    private MyBinder binder = new MyBinder();
    private Gson gson;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gson = new Gson();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * получает все альбомы
     *
     * @param onGetAllAlbumsListener коллбэк
     */
    @Override
    public void getAllAlbums(final OnGetAllAlbumsListener onGetAllAlbumsListener) {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Type photoAlbumsType = new TypeToken<Response<PhotoAlbums>>() {
                }.getType();
                Response<PhotoAlbums> albumsResponse = gson.fromJson(response.json.toString(), photoAlbumsType);
                if (onGetAllAlbumsListener != null) {
                    onGetAllAlbumsListener.onSuccess(albumsResponse.response.results);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                onGetAllAlbumsListener.onVKError(error);
            }
        });
    }

    @Override
    public boolean changeAlbumPrivacy(int i) {
        return false;
    }

    /**
     * Синхронизирует альбом
     *
     * @param photoAlbum объект альбома, который синхронизирует
     */
    @Override
    public void syncAlbum(PhotoAlbum photoAlbum) {

    }

    @Override
    public Photo getPhoto() {
        return null;
    }

    @Override
    public PhotoAlbum createAlbum() {
        return null;
    }

    public class MyBinder extends Binder {
        public SyncService getService() {
            return SyncServiceImpl.this;
        }
    }
}