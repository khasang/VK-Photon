package com.khasang.vkphoto.services;


import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.Photo;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {
    /**
     * Получает все альбомы
     *
     * @param onGetAllAlbumsRunnable
     */
    void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsRunnable);

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
      