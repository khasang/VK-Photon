package com.khasang.vkphoto.services;


import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.model.photo.Photo;
import com.khasang.vkphoto.model.album.PhotoAlbum;

/** интерфейс сервиса синхронизации */
public interface SyncService {
    /** Получает все альбомы */
    void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener);

    /** Регистрирует изменения доступа к альбому */
    boolean changeAlbumPrivacy(int i);

    /** Синхронизирует альбом */
    void syncAlbum(PhotoAlbum photoAlbum);
    /** Получает фотографию */
    Photo getPhoto();

    /** Создаёт альбом*/
    PhotoAlbum createAlbum();

}
      