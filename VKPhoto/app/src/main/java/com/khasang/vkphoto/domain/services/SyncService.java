package com.khasang.vkphoto.domain.services;


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

    void getAllAlbums();

    void getPhotosByAlbumId(int albumId);

    void addPhotos(List<Photo> listUploadedFiles, PhotoAlbum photoAlbum);

    void deleteVkPhotoById(int photoId);

    void deleteSelectedVkPhotos(List<Photo> photoList);

    void deleteVKAlbumById(int albumId);

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
}
      