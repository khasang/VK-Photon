package com.khasang.vkphoto.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.data.local.LocalAlbumSource;
import com.khasang.vkphoto.model.data.local.LocalDataSource;
import com.khasang.vkphoto.model.data.vk.VKDataSource;
import com.khasang.vkphoto.model.events.GetVKAlbumsEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.List;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private MyBinder binder = new MyBinder();
    private EventBus eventBus;
    private AsyncExecutor asyncExecutor;
    private LocalDataSource localDataSource;
    private VKDataSource vKDataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        asyncExecutor = AsyncExecutor.create();
        localDataSource = new LocalDataSource(getApplicationContext());
        vKDataSource = new VKDataSource();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void saveAlbum() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getAlbumSource().saveAlbum();
            }
        });
    }

    @Override
    public void getAllAlbums() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getAlbumSource().getAllAlbums();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKAlbumsEvent(GetVKAlbumsEvent getVKAlbumsEvent) {
        List<PhotoAlbum> vKphotoAlbumList = getVKAlbumsEvent.albumsList;
        LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
        List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllAlbums();
        for (PhotoAlbum photoAlbum : vKphotoAlbumList) {
            if (localAlbumsList.contains(photoAlbum)) {
                localAlbumSource.updateAlbum(photoAlbum);
            } else {
                localAlbumSource.saveAlbum(photoAlbum);
            }
        }

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

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }
}