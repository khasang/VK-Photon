package com.khasang.vkphoto.presentation.presenter.albums;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

public interface LocalAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(String title, String thumbPath);
    List<PhotoAlbum> getAllLocalAlbums();
}
