package com.khasang.vkphoto.domain.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.data.local.LocalDataSource;
import com.khasang.vkphoto.data.vk.VKDataSource;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalAlbumsEvent;
import com.khasang.vkphoto.domain.events.GetSwipeRefreshEvent;
import com.khasang.vkphoto.domain.events.GetVKAlbumsEvent;
import com.khasang.vkphoto.domain.events.LocalALbumEvent;
import com.khasang.vkphoto.domain.events.PhotosSynchedEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.events.VKAlbumEvent;
import com.khasang.vkphoto.domain.tasks.DownloadPhotoCallable;
import com.khasang.vkphoto.domain.tasks.SyncAlbumCallable;
import com.khasang.vkphoto.domain.tasks.UploadPhotoCallable;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.NetWorkUtils;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;

import java.io.File;
import java.util.ArrayList;
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
    private VKDataSource vkDataSource;
    private List<Future<Boolean>> futureList = new ArrayList<>();
    private Context context;
    private volatile Map<Integer, Future<Boolean>> futureMap = new HashMap<>();
    private Map<Long, Future<Photo>> futureMapUploadPhotos = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        asyncExecutor = AsyncExecutor.create();
        localDataSource = new LocalDataSource(getApplicationContext());
        vkDataSource = new VKDataSource();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        context = getApplicationContext();
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
                    final ExecutorService executor = Executors.newSingleThreadExecutor();
                    for (final PhotoAlbum photoAlbum : photoAlbumList) {
                        if (!futureMap.containsKey(photoAlbum.id)) {
                            RequestMaker.getVkPhotosByAlbumIdRequest(photoAlbum.id).executeSyncWithListener(new MyVkRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    try {
                                        Callable<Boolean> booleanCallable = new SyncAlbumCallable(photoAlbum, localDataSource,
                                                JsonUtils.getItems(response.json, Photo.class));
                                        futureMap.put(photoAlbum.id, executor.submit(booleanCallable));
                                        Logger.d("Got VKPhoto for photoAlbum " + photoAlbum.title);
                                    } catch (Exception e) {
                                        Logger.d(e.toString());
                                        sendError(ErrorUtils.JSON_PARSE_FAILED);
                                    }
                                }
                            });
                        }
                    }
                    execute();
                    if (futureMap.isEmpty()) {
                        eventBus.postSticky(new SyncAndTokenReadyEvent());
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
                        if (booleanFutureTask.get() && NetWorkUtils.isNetworkOnline(context)) {
                            iterator.remove();
                        }
                        Logger.d("exit get");
                    }
                } catch (ExecutionException e) {
                    Logger.d("canceled execution error");
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
                vkDataSource.getAlbumSource().addAlbum(title, description, privacy, commentPrivacy, localDataSource.getAlbumSource());
            }
        });
    }

    @Override
    public void uploadPhotos(final MultiSelector multiSelector, final List<Photo> localPhotoList, final long idPhotoAlbum) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                if (localPhotoList.size() > 0) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    for (Photo photo : localPhotoList) {
                        File file = new File(photo.filePath);
                        if (file.exists()) {
                            Callable<Photo> photoCallable = new UploadPhotoCallable(file, idPhotoAlbum, vkDataSource);
                            futureMapUploadPhotos.put(idPhotoAlbum, executor.submit(photoCallable));
                        }
                    }
                    execute();
                    executor.shutdown();
                }
            }

            private void execute() throws InterruptedException, java.util.concurrent.ExecutionException {
                Iterator<Map.Entry<Long, Future<Photo>>> iterator = futureMapUploadPhotos.entrySet().iterator();
                while (iterator.hasNext()) {
                    Future<Photo> photoFutureTask = iterator.next().getValue();
                    Logger.d(photoFutureTask.toString() + "startUpload");
                    if (photoFutureTask.get() != null) {
                        iterator.remove();
                    }
                    eventBus.postSticky(new SyncAndTokenReadyEvent());
                    Logger.d("exit get");
                }
            }
        });
        multiSelector.clearSelections();
//        eventBus.postSticky(new GotoBackFragmentEvent(context));
        eventBus.postSticky(new GetSwipeRefreshEvent(true));
    }

    @Override
    public void runSetContextEvent(Context context) {
        this.context = context;
    }

    @Override
    public void syncPhotos(final List<Photo> selectedPhotos, final PhotoAlbum photoAlbum) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                List<Future<File>> futures = new ArrayList<>();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                for (int i = 0; i < selectedPhotos.size(); i++) {
                    futures.add(executor.submit(new DownloadPhotoCallable(localDataSource.getPhotoSource(),
                            selectedPhotos.get(i), photoAlbum)));
                }
                execute(futures);
                if (futureList.isEmpty()) {
                    Logger.d("Full sync of photos complete");
                    eventBus.postSticky(new PhotosSynchedEvent(true));
                } else {
                    Logger.d("Not Full sync of photos complete");
                    eventBus.postSticky(new PhotosSynchedEvent(false));
                }
            }

            private void execute(List<Future<File>> futures) throws InterruptedException, ExecutionException {
                Logger.d("start downloading photos");
                Iterator<Future<File>> futureIterator = futures.iterator();
                while (futureIterator.hasNext()) {
                    if (futureIterator.next().get() != null) {
                        futureIterator.remove();
                    }
                }
            }
        });
    }

    @Override
    public void getAllLocalAlbumsList() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                Logger.d("SyncService getAllLocalAlbums");
                List<PhotoAlbum> albumsList = localDataSource.getAlbumSource().getAllLocalAlbumsList();
                if (albumsList.isEmpty()) {
                    EventBus.getDefault().postSticky(new ErrorEvent(77));
                } else {
                    EventBus.getDefault().postSticky(new GetLocalAlbumsEvent(albumsList));
                }
            }
        });
    }

    @Override
    public void editVkAlbum(final PhotoAlbum photoAlbum) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getAlbumSource().editVkAlbum(photoAlbum);
                vkDataSource.getAlbumSource().editVkAlbum(photoAlbum);
            }
        });
    }

    @Override
    public void editLocalOrSyncAlbum(final PhotoAlbum photoAlbum, final String newTitle) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getAlbumSource().editLocalOrSyncAlbum(photoAlbum, newTitle, localDataSource.getPhotoSource(),
                        localDataSource.getPhotoSource().getLocalPhotosByAlbumId(photoAlbum.id));
            }
        });
    }

    @Override
    public void editPrivacyOfAlbums(final List<PhotoAlbum> albumsList, final int newPrivacy) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (PhotoAlbum photoAlbum : albumsList) {
                    localDataSource.getAlbumSource().editPrivacyOfAlbum(photoAlbum, newPrivacy);
                    vkDataSource.getAlbumSource().editPrivacyOfAlbum(photoAlbum, newPrivacy);
                }
            }
        });
    }

    @Override
    public void createLocalAlbum(final String title) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getAlbumSource().createLocalAlbum(title);
            }
        });
    }

    @Override
    public void getAllVKAlbums() {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                Logger.d("SyncSerice getAllVKAlbums");
                vkDataSource.getAlbumSource().getAllVKAlbums();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKAlbumsEvent(GetVKAlbumsEvent getVKAlbumsEvent) {
        Logger.d("SyncSerice onGetVKAlbumsEvent");
        List<PhotoAlbum> vkAlbumList = getVKAlbumsEvent.albumsList;
        LocalAlbumSource localAlbumSource = localDataSource.getAlbumSource();
        List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllSynchronizedAlbums();
        for (int i = 0, vkAlbumListSize = vkAlbumList.size(); i < vkAlbumListSize; i++) {
            PhotoAlbum photoAlbum = vkAlbumList.get(i);
            if (localAlbumsList.contains(photoAlbum)) { //update existing album
                localAlbumSource.updateAlbum(photoAlbum, false);
            } else { //create new album
                localAlbumSource.saveAlbum(photoAlbum, false);
            }
        }

        //Update deleted from vk albums syncStatus and remove it from DB and from device
        if (localAlbumsList.removeAll(vkAlbumList)) {
            for (int i = 0, localAlbumsListSize = localAlbumsList.size(); i < localAlbumsListSize; i++) {
                PhotoAlbum photoAlbum = localAlbumsList.get(i);
                photoAlbum.syncStatus = Constants.SYNC_DELETED;
                localAlbumSource.updateAlbum(photoAlbum, true);
                deleteAlbumFromDbAndPhys(photoAlbum.id);
            }
        }
        eventBus.postSticky(new VKAlbumEvent());
    }

    @Override
    public void getVKPhotosByAlbumId(final int albumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                localDataSource.getPhotoSource().getSynchronizedPhotosByAlbumId(albumId);
                vkDataSource.getPhotoSource().getPhotosByAlbumId(albumId);
            }
        });
    }

    @Override
    public void deleteVkPhotoById(final int photoId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                vkDataSource.getPhotoSource().deletePhoto(photoId);
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
                eventBus.postSticky(new SyncAndTokenReadyEvent());
            }
        });
    }

    @Override
    public void deleteAllVkPhotoAlbums() {
        final List<PhotoAlbum> photoAlbumList = localDataSource.getAlbumSource().getAllSynchronizedAlbums();
        for (PhotoAlbum photoAlbum : photoAlbumList) {
            deleteAlbumFromDbAndPhys(photoAlbum.getId());
        }
    }

    @Override
    public void deleteSelectedVkPhotoAlbums(final List<PhotoAlbum> photoAlbumList) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                for (PhotoAlbum photoAlbum : photoAlbumList) {
                    deleteAlbumFromDbAndPhys(photoAlbum.getId());
                    vkDataSource.getAlbumSource().deleteAlbumFromVkServ(photoAlbum.getId());
                    try {
                        TimeUnit.MILLISECONDS.sleep(340);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void deleteAlbumFromDbAndPhys(final int photoAlbumId) {
        asyncExecutor.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                List<PhotoAlbum> localAlbumsList = localDataSource.getAlbumSource().getAllSynchronizedAlbums();
                for (PhotoAlbum localAlbum : localAlbumsList) {
                    if (localAlbum.getId() == photoAlbumId) {
                        if (localAlbum.syncStatus == Constants.SYNC_STARTED ||
                                localAlbum.syncStatus == Constants.SYNC_SUCCESS ||
                                localAlbum.syncStatus == Constants.SYNC_FAILED) {
                            FileManager.deleteAlbumDirectory(localAlbum.filePath);
                        }
                        localDataSource.getAlbumSource().deleteAlbumFromDbAndPhys(localAlbum);
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
                localDataSource.getPhotoSource().deletePhotoListFromDB(deletePhotoList);
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
                    //сначала удалим альбомы из папки на девайсе (не папки нашего приложения)
                    List<Photo> deletePhotoList = localDataSource.getPhotoSource().getLocalPhotosByAlbumId(photoAlbum.id);
                    localDataSource.getPhotoSource().deleteLocalPhotos(deletePhotoList);
                    eventBus.post(new LocalALbumEvent());
                    //потом удалим записи из бд нашего приложения.
                    //это нужно только для тех альбомов, которые появились на устройстве в результате синхронизации с ВК
                    try {
                        deleteAlbumFromDbAndPhys(photoAlbum.id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.d("error while deleting photoAlbum: " + photoAlbum.filePath);
                    }
                }
            }
        });
        //и вообще мне очень не нравится, что мы всю реализацию пихаем в SyncServiceImpl. см к примеру deleteSelectedLocalPhotos
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
                    eventBus.postSticky(new SyncAndTokenReadyEvent());
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