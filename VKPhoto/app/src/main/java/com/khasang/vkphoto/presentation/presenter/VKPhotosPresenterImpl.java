package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.view.VkPhotosView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class VKPhotosPresenterImpl implements VKPhotosPresenter {
    private VkPhotosView vkPhotosView;
    private VkPhotosInteractor vkPhotosInteractor;

    public VKPhotosPresenterImpl(VkPhotosView vkPhotosView, SyncServiceProvider syncServiceProvider) {
        this.vkPhotosView = vkPhotosView;
        vkPhotosInteractor = new VkPhotosInteractorImpl(syncServiceProvider);
    }


    @Override
    public void getPhotosByAlbumId(int albumId) {
        vkPhotosInteractor.getPhotosByAlbumId(albumId);
    }

    @Override
    public void initialize() {
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
    public void OnLocalAlbumEvent(LocalAlbumEvent localAlbumEvent) {
        vkPhotosView.displayVkPhotosByAlbumId();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkPhotosView.showError(errorEvent.errorMessage);
    }
}
