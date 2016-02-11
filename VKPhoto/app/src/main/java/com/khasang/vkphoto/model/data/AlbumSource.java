package com.khasang.vkphoto.model.data;

public interface AlbumSource {
    void saveAlbum();

    void updateAlbum();

    void deleteAlbum();

    void deleteAlbums();

    void getAlbumById();

    void getAllAlbums();
}
