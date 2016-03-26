package com.khasang.vkphoto.domain.services;


import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {
    /**
     * Создаёт пустой альбом в ВК и на устройстве
     * @param title
     * @param description
     * @param privacy
     * @param commentPrivacy
     */
    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);

    void getAllVKAlbums();

    void getVKPhotosByAlbumId(int albumId);

    void getAllLocalAlbumsList();

    void uploadPhotos(final MultiSelector multiSelector, final List<Photo> localPhotoList, final long idPhotoAlbum);

    void deleteVkPhotoById(int photoId);

    void deleteSelectedVkPhotos(List<Photo> photoList);

    void deleteSelectedVkPhotoAlbums(List<PhotoAlbum> photoAlbumList);

    /**
     * Регистрирует изменения доступа к альбому
     */
    boolean changeAlbumPrivacy(int i);

    /**
     * Синхронизирует альбом
     */
    void syncAlbums(List<PhotoAlbum> photoAlbumList);

    /**
     * Получает фотографию
     */
    Photo getPhoto();

    /**
     * Создаёт альбом
     */
    PhotoAlbum createAlbum();

    void getLocalPhotosByAlbumId(int albumId);

    void deleteSelectedLocalPhotos(List<Photo> deletePhotoList);

    void deleteSelectedLocalPhotoAlbums(List<PhotoAlbum> deleteAlbumsList);

    void startSync();

    void cancelAlbumsSync(List<PhotoAlbum> selectedAlbums);

    void editAlbum(int albumId, String title, String description);

    void editPrivacyAlbum(int albumId, int privacy);

    void editLocalAlbum(int albumId, String title);

    void runSetContextEvent(Context context);
}
      