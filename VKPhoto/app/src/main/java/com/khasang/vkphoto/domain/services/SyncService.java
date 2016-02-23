package com.khasang.vkphoto.domain.services;


import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;
import java.util.Vector;

/**
 * интерфейс сервиса синхронизации
 */
public interface SyncService {
    /**
     * Создаёт альбом в ВК и на устройство
     * @param title
     * @param description
     * @param listUploadedFiles
     * @param privacy
     * @param comment_privacy
     */
    void addAlbum(final String title, final String description,
                  final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy);

    void getAllAlbums();

    void getPhotosByAlbumId(int albumId);

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
      