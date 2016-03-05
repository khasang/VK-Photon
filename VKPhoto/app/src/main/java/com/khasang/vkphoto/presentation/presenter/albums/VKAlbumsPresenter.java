package com.khasang.vkphoto.presentation.presenter.albums;

public interface VKAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);
}
      