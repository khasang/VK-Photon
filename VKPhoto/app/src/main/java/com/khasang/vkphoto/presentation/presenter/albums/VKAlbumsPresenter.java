package com.khasang.vkphoto.presentation.presenter.albums;

import com.bignerdranch.android.multiselector.MultiSelector;

public interface VKAlbumsPresenter extends AlbumsPresenter {
    void syncAlbums(MultiSelector multiSelector);

    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);
}
      