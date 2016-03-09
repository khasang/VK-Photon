package com.khasang.vkphoto.domain.interactors;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;

import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalPhotosInteractorImpl implements LocalPhotosInteractor {

    public LocalPhotosInteractorImpl() {
    }

    @Override
    public List<Photo> getPhotosByAlbum(PhotoAlbum photoAlbum, Context context) {
        LocalPhotoSource localAlbumSource = new LocalPhotoSource(context);
        return localAlbumSource.getPhotosByAlbumPath(photoAlbum.filePath);
    }

    @Override
    public void addLocalPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum) {
        Logger.d("user wants to addLocalPhotos");
    }

    @Override
    public void deleteSelectedLocalPhotos(MultiSelector multiSelector, List<Photo> photoList) {
        Logger.d("user wants to deleteSelectedLocalPhotos");
    }
}
