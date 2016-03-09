package com.khasang.vkphoto.domain.interactors;

import android.content.Context;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public interface LocalPhotosInteractor {
    List<Photo> getPhotosByAlbum(PhotoAlbum photoAlbum);
    void addLocalPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum);
    void deleteSelectedLocalPhotos(MultiSelector multiSelector, List<Photo> photoList);
}
