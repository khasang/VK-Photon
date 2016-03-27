package com.khasang.vkphoto.presentation.presenter.albums;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

public interface LocalAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(String title);

    List<PhotoAlbum> getAllLocalAlbums();

    void editLocalOrSyncAlbum(PhotoAlbum photoAlbum, String newTitle);
}
