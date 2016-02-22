package com.khasang.vkphoto.domain.services;


import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {

    void getAllAlbums();

    void getPhotosByAlbumId(int albumId);

    void deletePhotoById(int photoId);

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

}
      