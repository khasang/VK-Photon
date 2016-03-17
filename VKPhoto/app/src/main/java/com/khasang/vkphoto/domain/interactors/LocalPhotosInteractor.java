package com.khasang.vkphoto.domain.interactors;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;

import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public interface LocalPhotosInteractor {
    void getPhotosByAlbumId(int albumId);

    void savePhotos(final MultiSelector multiSelector, final long idVKPhotoAlbum);

    void deleteSelectedLocalPhotos(MultiSelector multiSelector, List<Photo> photoList);
}
