package com.khasang.vkphoto.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.domain.runnables.GetAllAlbumsRunnable;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.executor.ThreadExecutor;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.Photo;

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
    public void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener) {
        executor.execute(new GetAllAlbumsRunnable(mainThread, onGetAllAlbumsListener));
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