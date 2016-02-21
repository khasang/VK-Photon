package com.khasang.vkphoto.domain.services;


import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {
    void saveAlbum();

    void getAllAlbums();

    void getPhotosByAlbumId(int albumId);

    /**
     * Регистрирует изменения доступа к альбому
     */
    boolean changeAlbumPrivacy(int i);

    /**
     * Синхронизирует альбом
     */
    void syncAlbum(PhotoAlbum photoAlbum);

    /**
     * Получает фотографию
     */
    Photo getPhoto();

    /**
     * Создаёт альбом
     */
    PhotoAlbum createAlbum();

}
      