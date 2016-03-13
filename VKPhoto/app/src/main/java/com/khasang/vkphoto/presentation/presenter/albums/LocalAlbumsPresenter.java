package com.khasang.vkphoto.presentation.presenter.albums;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;

public interface LocalAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(String title, String thumbPath);
    void deleteAlbums(MultiSelector multiSelector, Context context);
}
