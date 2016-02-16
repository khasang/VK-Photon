package com.khasang.vkphoto.ui.presenter;

import android.content.Context;

import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.model.events.ErrorEvent;
import com.khasang.vkphoto.model.events.GetVkSaveAlbumEvent;
import com.khasang.vkphoto.model.events.LocalAlbumEvent;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.view.VkAlbumsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VKAlbumsPresenterImpl implements VKAlbumsPresenter {
    private Navigator navigator;
    private VkAlbumsView vkAlbumsView;
    private VkAlbumsInteractor vkAlbumsInteractor;

    public VKAlbumsPresenterImpl(VkAlbumsView vkAlbumsView, SyncServiceProvider syncServiceProvider, Navigator navigator, Context context) {
        this.vkAlbumsView = vkAlbumsView;
        this.navigator = navigator;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider, context);
    }

    @Override
    public void saveAlbum() {
        vkAlbumsInteractor.saveAlbum();
    }

    @Override
    public void getAllAlbums() {
        vkAlbumsInteractor.getAllAlbums();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveAlbumEvent(GetVkSaveAlbumEvent getVkSaveAlbumEvent) {
        vkAlbumsView.displayVkSaveAlbum(getVkSaveAlbumEvent.photoAlbum);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnLocalAlbumEvent(LocalAlbumEvent localAlbumEvent) {
        vkAlbumsView.displayVkAlbums();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkAlbumsView.showError(errorEvent.errorMessage);
    }
}
      