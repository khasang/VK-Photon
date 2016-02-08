package com.khasang.vkphoto.services;


import com.khasang.vkphoto.domain.interfaces.OnGetAllAlbumsListener;
import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.PhotoAlbum;

public interface SyncService {
    void getAllAlbums(OnGetAllAlbumsListener onGetAllAlbumsListener);

    boolean changeAlbumPrivacy(int i);

    void syncAlbum(PhotoAlbum photoAlbum);

    Photo getPhoto();

    PhotoAlbum createAlbum();

}
      