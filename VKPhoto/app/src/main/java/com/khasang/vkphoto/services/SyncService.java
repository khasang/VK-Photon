package com.khasang.vkphoto.services;


import com.khasang.vkphoto.domain.entities.Photo;
import com.khasang.vkphoto.domain.entities.PhotoAlbum;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {
    void saveAlbum();

    void getAllAlbums();

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
      