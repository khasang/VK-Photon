package com.khasang.vkphoto.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.khasang.vkphoto.domain.RequestMaker;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.executor.ThreadExecutor;
import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.events.ErrorEvent;
import com.khasang.vkphoto.model.events.GetVkAlbumsEvent;
import com.khasang.vkphoto.util.JsonUtils;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.List;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private ThreadExecutor executor = new ThreadExecutor();
    private MainThread mainThread = new MainThreadImpl();
    private MyBinder binder = new MyBinder();
    private EventBus eventBus;
    private AsyncExecutor asyncExecutor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        eventBus = EventBus.getDefault();
        asyncExecutor = AsyncExecutor.create();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void getAllAlbums() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                RequestMaker.getAllVkAlbums(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        final List<VKApiPhotoAlbum> vkApiPhotoAlbums;
                        try {
                            vkApiPhotoAlbums = JsonUtils.getItems(response.json, VKApiPhotoAlbum.class);
                            eventBus.postSticky(new GetVkAlbumsEvent(vkApiPhotoAlbums));
                        } catch (Exception e) {
                            sendError(e.toString());
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        sendError(error.toString());
                    }

                    void sendError(String s) {
                        eventBus.postSticky(new ErrorEvent(s));
                    }
                });
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