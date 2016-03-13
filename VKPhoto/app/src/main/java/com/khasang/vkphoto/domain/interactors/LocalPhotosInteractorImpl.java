package com.khasang.vkphoto.domain.interactors;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalPhotosInteractorImpl implements LocalPhotosInteractor {
    LocalPhotoSource localPhotoSource;

    public LocalPhotosInteractorImpl(Context context) {
        localPhotoSource = new LocalPhotoSource(context);
    }

    @Override
    public List<Photo> getPhotosByAlbum(PhotoAlbum photoAlbum, Context context) {
        return localPhotoSource.getPhotosByAlbumPath(photoAlbum.filePath, context);
    }

    @Override
    public void addLocalPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum) {
        Logger.d("user wants to addLocalPhotos");
        Logger.d("no body");
    }

    @Override
    public void deleteSelectedLocalPhotos(MultiSelector multiSelector, List<Photo> photoList, Context context) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        List<Photo> deletePhotoList = new ArrayList<>();
        if (photoList != null) {
            for (int i = 0; i < selectedPositions.size(); i++) {
                Integer position = selectedPositions.get(i);
                deletePhotoList.add(photoList.get(position));
            }
            localPhotoSource.deleteLocalPhotos(deletePhotoList, context);
        }
    }
}
