package com.khasang.vkphoto.domain.tasks;

import android.support.annotation.NonNull;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalDataSource;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SyncAlbumCallable implements Callable<Boolean> {
    private LocalDataSource localDataSource;
    private PhotoAlbum photoAlbum;
    private boolean success = true;

    public SyncAlbumCallable(PhotoAlbum photoAlbum, LocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
        this.photoAlbum = photoAlbum;
    }

    @Override
    public Boolean call() throws Exception {
        Logger.d("Entered SyncAlbumCallable " + photoAlbum.title + " call");
        final LocalPhotoSource localPhotoSource = localDataSource.getPhotoSource();
        VKRequest vkRequest = getVkRequest();
        vkRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final List<Photo> vkPhotoList;
                try {
                    vkPhotoList = JsonUtils.getItems(response.json, Photo.class);
                    Logger.d("Got VKPhoto for photoAlbum " + photoAlbum.title);
                    removeDownloadedImages(vkPhotoList, localPhotoSource);
                    ExecutorService executor = Executors.newFixedThreadPool(3);
                    List<Future<Boolean>> futureList = new ArrayList<>();
                    for (int i = 0; i < vkPhotoList.size(); i++) {
                        Photo photo = vkPhotoList.get(i);
                        DownloadPhotoCallable downloadPhotoCallable = new DownloadPhotoCallable(localPhotoSource, photo, photoAlbum);
                        futureList.add(executor.submit(downloadPhotoCallable));
                    }
                    for (Future<Boolean> booleanFuture : futureList) {
                        booleanFuture.get();
                    }
                    executor.shutdown();
                    photoAlbum.syncStatus = Constants.SYNC_SUCCESS;
                    localDataSource.getAlbumSource().updateAlbum(photoAlbum);
                } catch (Exception e) {
                    sendError(e.toString());
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendError(error.toString());
                success = false;
            }
        });
        return success;
    }

    private void removeDownloadedImages(List<Photo> vkPhotoList, LocalPhotoSource localPhotoSource) {
        List<Photo> localPhotoList = localPhotoSource.getPhotosByAlbum(photoAlbum);
        for (int i = 0; i < localPhotoList.size(); i++) {
            Photo photo = localPhotoList.get(i);
            if (new File(photo.filePath).exists()) {
                vkPhotoList.remove(photo);
            }
        }
    }

    @NonNull
    private VKRequest getVkRequest() {
        VKRequest vkRequest = RequestMaker.getVkPhotosByAlbumIdRequest(photoAlbum.id);
        vkRequest.attempts = 5;
        return vkRequest;
    }

    private void sendError(String s) {
        EventBus.getDefault().postSticky(new ErrorEvent(s));
    }

}
