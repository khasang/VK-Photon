package com.khasang.vkphoto.ui.presenter;

import com.khasang.vkphoto.domain.interactors.MainInteractor;
import com.khasang.vkphoto.domain.interactors.MainInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.OnGetAllAlbumsListener;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.view.MainView;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;

import java.util.List;

public class MainPresenterImpl implements MainPresenter, OnGetAllAlbumsListener {
    private Navigator navigator;
    private SyncServiceProvider syncServiceProvider;
    private MainView mainView;
    private MainInteractor mainInteractor;

    public MainPresenterImpl(MainView mainView, SyncServiceProvider syncServiceProvider, Navigator navigator) {
        this.mainView = mainView;
        this.syncServiceProvider = syncServiceProvider;
        this.navigator = navigator;
        mainInteractor = new MainInteractorImpl(syncServiceProvider);
    }

    @Override
    public void getAllAlbums() {
        mainInteractor.getAllAlbums(this);
    }

    @Override
    public void onSuccess(List<PhotoAlbum> photoAlbums) {
        mainView.displayVkAlbums(photoAlbums);
    }

    @Override
    public void onVKError(VKError e) {
        Logger.d(e.errorMessage);
        mainView.showConnectionError();
    }

    @Override
    public void onSyncServiceError() {

    }
}
      