package com.khasang.vkphoto.domain.tasks;

import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;

import java.io.File;
import java.util.concurrent.Callable;

public class DownloadPhotoCallable implements Callable<File> {
    public static final int ATTEMPT_COUNT = 3;
    private LocalPhotoSource localPhotoSource;
    private Photo photo;
    private PhotoAlbum photoAlbum;

    public DownloadPhotoCallable(LocalPhotoSource localPhotoSource, Photo photo, PhotoAlbum photoAlbum) {
        this.localPhotoSource = localPhotoSource;
        this.photo = photo;
        this.photoAlbum = photoAlbum;
    }

    @Override
    public File call() throws Exception {
        Logger.d("start save photo " + photo.id);
        File file;
        for (int i = 0; i < ATTEMPT_COUNT && !Thread.currentThread().isInterrupted(); i++) {
            if ((file = localPhotoSource.savePhotoToAlbum(photo, photoAlbum)).exists()) {
                return file;
            }
        }
        return null;
    }
}
