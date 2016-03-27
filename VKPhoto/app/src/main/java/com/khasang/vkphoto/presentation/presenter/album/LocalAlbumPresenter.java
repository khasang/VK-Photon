package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;

public interface LocalAlbumPresenter extends AlbumPresenter {
    void gotoBack(Context context);

    void runSetContextEvent();
}
