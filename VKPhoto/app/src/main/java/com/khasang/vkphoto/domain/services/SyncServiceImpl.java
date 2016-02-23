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
import com.khasang.vkphoto.domain.events.GetVkAddAlbumEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.List;
import java.util.Vector;

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

    public void syncAlbum(final PhotoAlbum photoAlbum) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getPhotoSource().getPhotosByAlbumId(photoAlbum.id);
            }
        });
    }
    @Override
    public void syncAlbums(final List<PhotoAlbum> photoAlbumList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (PhotoAlbum  photoAlbum : photoAlbumList) {
                    vKDataSource.getPhotoSource().getPhotosByAlbumId(photoAlbum.id);
                    Logger.d(photoAlbum.title);
                }
            }
        });
    }

    @Override
    public void addAlbum(final String title, final String description,
                         final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getAlbumSource().addAlbum(title, description, listUploadedFiles, privacy, comment_privacy);
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
    public void onGetVKAddAlbumEvent(GetVkAddAlbumEvent getVkAddAlbumEvent) {
        PhotoAlbum vkAddAlbum = getVkAddAlbumEvent.photoAlbum;
        LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
        List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllAlbums();
        if (localAlbumsList.contains(vkAddAlbum)) { //update existing albums
            localAlbumSource.updateAlbum(vkAddAlbum);
        } else { //сreate new albums
            localAlbumSource.addAlbum(vkAddAlbum);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKAlbumsEvent(GetVKAlbumsEvent getVKAlbumsEvent) {
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