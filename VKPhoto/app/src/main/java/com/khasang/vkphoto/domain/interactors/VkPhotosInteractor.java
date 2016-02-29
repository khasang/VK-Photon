package com.khasang.vkphoto.domain.interactors;

<<<<<<< HEAD
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
=======
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
>>>>>>> feature/iss28-add-multiselect

import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VkPhotosInteractor {
    void getPhotosByAlbumId(int albumId);

<<<<<<< HEAD
    void deletePhotoById(int photoId);

    void addPhotos(List<String> listUploadedFiles, PhotoAlbum photoAlbum);
=======
    void deleteSelectedVkPhotos(MultiSelector multiSelector, List<Photo> photoList);
>>>>>>> feature/iss28-add-multiselect
}
