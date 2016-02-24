package com.khasang.vkphoto.domain.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.data.local.LocalDataSource;
import com.khasang.vkphoto.data.vk.VKDataSource;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.tasks.SyncAlbumCallable;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private MyBinder binder = new MyBinder();
    private EventBus eventBus;
    private AsyncExecutor asyncExecutor;
    private LocalDataSource localDataSource;
    private VKDataSource vKDataSource;
    private List<Future<Boolean>> futures = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        asyncExecutor = AsyncExecutor.create();
        localDataSource = new LocalDataSource(getApplicationContext());
        vKDataSource = new VKDataSource();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        ArrayBlockingQueue<String> strings = new ArrayBlockingQueue<>(15);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void syncAlbums(final List<PhotoAlbum> photoAlbumList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                for (PhotoAlbum photoAlbum : photoAlbumList) {
                    Callable<Boolean> booleanCallable = new SyncAlbumCallable(photoAlbum, localDataSource.getPhotoSource());
                    futures.add(executor.submit(booleanCallable));
                }
                Iterator<Future<Boolean>> iterator = futures.iterator();
                while (iterator.hasNext()) {
                    Future<Boolean> booleanFutureTask = iterator.next();
                    if (booleanFutureTask.get()) {
                        futures.remove(booleanFutureTask);
                    }
                    Logger.d("exit get");
                }
            }
        });
    }

    @Override
    public void getAllAlbums() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                Logger.d("SyncSerice getAllAlbums");
                vKDataSource.getAlbumSource().getAllAlbums();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKAlbumsEvent(GetVKAlbumsEvent getVKAlbumsEvent) {
        Logger.d("SyncSerice onGetVKAlbumsEvent");
        List<PhotoAlbum> vKphotoAlbumList = getVKAlbumsEvent.albumsList;
        LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
        List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllAlbums();
        for (int i = 0, vKphotoAlbumListSize = vKphotoAlbumList.size(); i < vKphotoAlbumListSize; i++) {
            PhotoAlbum photoAlbum = vKphotoAlbumList.get(i);
            if (localAlbumsList.contains(photoAlbum)) { //update existing albums
                localAlbumSource.updateAlbum(photoAlbum);
            } else { //сreate new albums
                localAlbumSource.saveAlbum(photoAlbum);
            }
        }

        //Update deleted from vk albums syncStatus
        if (localAlbumsList.removeAll(vKphotoAlbumList)) {
            for (int i = 0, localAlbumsListSize = localAlbumsList.size(); i < localAlbumsListSize; i++) {
                PhotoAlbum photoAlbum = localAlbumsList.get(i);
                photoAlbum.syncStatus = Constants.SYNC_DELETED;
                localAlbumSource.updateAlbum(photoAlbum);
            }
        }
    }

    @Override
    public void getPhotosByAlbumId(final int albumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getPhotoSource().getPhotosByAlbumId(albumId);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        List<Photo> vkPhotoList = getVKPhotosEvent.photosList;
    }

    @Override
    public void deleteVkPhotoById(final int photoId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getPhotoSource().deletePhoto(photoId);
            }
        });
    }

    @Override
    public void deleteVKAlbumById(final int albumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getAlbumSource().deleteAlbumById(albumId);
            }
        });
    }

    @Override
    public boolean changeAlbumPrivacy(int i) {
        return false;
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