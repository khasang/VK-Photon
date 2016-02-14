package com.khasang.vkphoto.services;


import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.PhotoAlbum;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {

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
      