package com.khasang.vkphoto.ui.presenter;

import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListenerImpl;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.view.VkAlbumsView;

public class VKAlbumsPresenterImpl implements VKAlbumsPresenter {
    private Navigator navigator;
    private VkAlbumsView vkAlbumsView;
    private VkAlbumsInteractor vkAlbumsInteractor;

    public VKAlbumsPresenterImpl(VkAlbumsView vkAlbumsView, SyncServiceProvider syncServiceProvider, Navigator navigator) {
        this.vkAlbumsView = vkAlbumsView;
        this.navigator = navigator;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider);
    }

    @Override
    public void getAllAlbums() {
        vkAlbumsInteractor.getAllAlbums(new OnGetAllAlbumsListenerImpl(vkAlbumsView));
    }

}
      