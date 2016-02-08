package com.khasang.vkphoto.ui.presenter;

import com.khasang.vkphoto.domain.interactors.MainInteractor;
import com.khasang.vkphoto.domain.interactors.MainInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListenerImpl;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private Navigator navigator;
    private MainView mainView;
    private MainInteractor mainInteractor;

    public MainPresenterImpl(MainView mainView, SyncServiceProvider syncServiceProvider, Navigator navigator) {
        this.mainView = mainView;
        this.navigator = navigator;
        mainInteractor = new MainInteractorImpl(syncServiceProvider);
    }

    @Override
    public void getAllAlbums() {
        mainInteractor.getAllAlbums(new OnGetAllAlbumsListenerImpl(mainView));
    }
}
      