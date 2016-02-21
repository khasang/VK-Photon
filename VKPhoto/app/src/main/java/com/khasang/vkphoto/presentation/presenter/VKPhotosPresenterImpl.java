package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;
import android.util.Log;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVkSaveAlbumEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;
import com.khasang.vkphoto.presentation.view.VkPhotosView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Anton on 21.02.2016.
 */
public class VKPhotosPresenterImpl implements VKPhotosPresenter {
    private Navigator navigator;
    private VkPhotosView vkPhotosView;
    private VkPhotosInteractor vkPhotosInteractor;

    public VKPhotosPresenterImpl(VkPhotosView vkPhotosView, SyncServiceProvider syncServiceProvider, Navigator navigator, Context context) {
        this.vkPhotosView = vkPhotosView;
        this.navigator = navigator;
        vkPhotosInteractor = new VkPhotosInteractorImpl(syncServiceProvider, context);
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
