package com.khasang.vkphoto.presentation.presenter.albums;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

public interface VKAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(final String title, final String description, final int privacy, final int commentPrivacy);

    void getAllVKAlbums();

    void editVkAlbum(PhotoAlbum photoAlbum);

    void editPrivacyOfAlbums(List<PhotoAlbum> albumsList, int newPrivacy);
}
      