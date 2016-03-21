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
import com.khasang.vkphoto.domain.events.LocalALbumEvent;
import com.khasang.vkphoto.domain.events.VKAlbumEvent;
import com.khasang.vkphoto.domain.tasks.SyncAlbumCallable;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private MyBinder binder = new MyBinder();
    private EventBus eventBus;
    private AsyncExecutor asyncExecutor;
    private LocalDataSource localDataSource;
    private VKDataSource vKDataSource;
    private Map<Integer, Future<Boolean>> futureMap = new HashMap<>();

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
    public void syncAlbums(final List<PhotoAlbum> photoAlbumList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                if (photoAlbumList.size() > 0) {
                    localDataSource.getAlbumSource().setSyncStatus(photoAlbumList, Constants.SYNC_STARTED);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    for (PhotoAlbum photoAlbum : photoAlbumList) {
                        Callable<Boolean> booleanCallable = new SyncAlbumCallable(photoAlbum, localDataSource);
                        futureMap.put(photoAlbum.id, executor.submit(booleanCallable));
                    }
                    execute();
                    if (futureMap.isEmpty()) {
                        Logger.d("full sync success");
                    } else {
                        Logger.d("full sync fail");
                    }
                    executor.shutdown();
                }
            }

            private void execute() throws InterruptedException, java.util.concurrent.ExecutionException {
                try {
                    Iterator<Map.Entry<Integer, Future<Boolean>>> iterator = futureMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Future<Boolean> booleanFutureTask = iterator.next().getValue();
                        Logger.d(booleanFutureTask.toString() + "startSync");
                        if (booleanFutureTask.get()) {
                            iterator.remove();
                        }
                        Logger.d("exit get");
                    }
                } catch (ExecutionException e) {
                    Logger.d("canceled execution error");
                    execute();
                }
            }

        });
    }


    @Override
    public void addAlbum(final String title, final String description,
                         final int privacy, final int commentPrivacy) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getAlbumSource().addAlbum(title, description, privacy, commentPrivacy, localDataSource.getAlbumSource());
            }
        });
    }

    @Override
    public void getAllVKAlbums() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                Logger.d("SyncSerice getAllVKAlbums");
                vKDataSource.getAlbumSource().getAllVKAlbums();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKAlbumsEvent(GetVKAlbumsEvent getVKAlbumsEvent) {
        Logger.d("SyncSerice onGetVKAlbumsEvent");
        List<PhotoAlbum> vKphotoAlbumList = getVKAlbumsEvent.albumsList;
        LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
        List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllSynchronizedAlbums();
        for (int i = 0, vKphotoAlbumListSize = vKphotoAlbumList.size(); i < vKphotoAlbumListSize; i++) {
            PhotoAlbum photoAlbum = vKphotoAlbumList.get(i);
            if (localAlbumsList.contains(photoAlbum)) { //update existing albums
                localAlbumSource.updateAlbum(photoAlbum, false);
            } else { //сreate new albums
                localAlbumSource.saveAlbum(photoAlbum, false);
            }
        }

        //Update deleted from vk albums syncStatus
        if (localAlbumsList.removeAll(vKphotoAlbumList)) {
            for (int i = 0, localAlbumsListSize = localAlbumsList.size(); i < localAlbumsListSize; i++) {
                PhotoAlbum photoAlbum = localAlbumsList.get(i);
                photoAlbum.syncStatus = Constants.SYNC_DELETED;
                localAlbumSource.updateAlbum(photoAlbum, true);
            }
        }
        EventBus.getDefault().postSticky(new VKAlbumEvent());
    }

    @Override
    public void getVKPhotosByAlbumId(final int albumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getPhotoSource().getSynchronizedPhotosByAlbumId(albumId);
                vKDataSource.getPhotoSource().getPhotosByAlbumId(albumId);
            }
        });
    }

    @Override
    public void addPhotos(final List<Photo> listUploadedFiles, final PhotoAlbum photoAlbum) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vKDataSource.getPhotoSource().savePhotos(listUploadedFiles, photoAlbum, localDataSource.getAlbumSource());
            }
        });
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
    public void deleteSelectedVkPhotos(final List<Photo> photoList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (Photo photo : photoList) {
                    deleteVkPhotoById(photo.getId());
                    try {
                        TimeUnit.MILLISECONDS.sleep(340);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
    public void deleteAlbumFromDbById(final int photoAlbumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
                List<PhotoAlbum> localAlbumsList = localAlbumSource.getAllSynchronizedAlbums();
                for (PhotoAlbum localAlbum : localAlbumsList) {
                    if (localAlbum.getId() == photoAlbumId) {
                        if (localAlbum.syncStatus == Constants.SYNC_STARTED) {
                            FileManager.deleteAlbumDirectory(localAlbum.filePath);
                        }
                        localAlbumSource.deleteAlbum(localAlbum);
                    }
                }
            }
        });
    }

    @Override
    public void deleteSelectedVkPhotoAlbums(final List<PhotoAlbum> photoAlbumList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (PhotoAlbum photoAlbum : photoAlbumList) {
                    deleteAlbumFromDbById(photoAlbum.getId());
                    deleteVKAlbumById(photoAlbum.getId());
                    try {
                        TimeUnit.MILLISECONDS.sleep(340);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public void getLocalPhotosByAlbumId(final int albumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getPhotoSource().getLocalPhotosByAlbumId(albumId);
            }
        });
    }

    @Override
    public void deleteSelectedLocalPhotos(final List<Photo> deletePhotoList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getPhotoSource().deleteLocalPhotos(deletePhotoList);
            }
        });
    }

    @Override
    public void deleteSelectedLocalPhotoAlbums(final List<PhotoAlbum> deleteAlbumsList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (PhotoAlbum photoAlbum : deleteAlbumsList) {
                    Logger.d("now deleting photoAlbum: " + photoAlbum.filePath);
                    List<Photo> deletePhotoList = localDataSource.getPhotoSource().getLocalPhotosByAlbumId(photoAlbum.id);
                    localDataSource.getPhotoSource().deleteLocalPhotos(deletePhotoList);
                    eventBus.post(new LocalALbumEvent());
                }
            }
        });
    }

    @Override
    public void startSync() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                syncAlbums(localDataSource.getAlbumSource().getAlbumsToSync());
            }
        });
    }

    @Override
    public void cancelAlbumsSync(final List<PhotoAlbum> selectedAlbums) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                try {
                    for (PhotoAlbum photoAlbum : selectedAlbums) {
                        Future<Boolean> booleanFuture = futureMap.get(photoAlbum.id);
                        if (booleanFuture != null) {
                            booleanFuture.cancel(true);
                            Logger.d(booleanFuture.toString() + " canceled");
                            futureMap.remove(photoAlbum.id);
                        }
                    }
                    for (PhotoAlbum photoAlbum : selectedAlbums) {
                        photoAlbum.syncStatus = Constants.SYNC_NOT_STARTED;
                        localDataSource.getAlbumSource().updateAlbum(photoAlbum, true);
                    }
                } catch (Exception e) {
                    Logger.d(e.toString());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public SyncService getService() {
            return SyncServiceImpl.this;
        }
    }
}