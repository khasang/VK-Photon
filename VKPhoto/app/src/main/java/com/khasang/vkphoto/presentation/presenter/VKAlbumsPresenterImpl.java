package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetAlbumEvent;
import com.khasang.vkphoto.domain.events.GetVkSaveAlbumEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAddAlbumView;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VKAlbumsPresenterImpl implements VKAlbumsPresenter {
    private VkAlbumsView vkAlbumsView;
    private VkAddAlbumView vKAddAlbumView;
    private VkAlbumsInteractor vkAlbumsInteractor;

    public VKAlbumsPresenterImpl(VkAlbumsView vkAlbumsView, SyncServiceProvider syncServiceProvider) {
        this.vkAlbumsView = vkAlbumsView;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider);
    }

    public VKAlbumsPresenterImpl(VkAddAlbumView vkAddAlbumView, SyncServiceProvider syncServiceProvider) {
        this.vKAddAlbumView = vkAddAlbumView;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider);
    }


    @Override
    public void syncAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.syncAlbums(multiSelector, vkAlbumsView.getAdapterCursor());
    }

    @Override
    public void getAllAlbums() {
        vkAlbumsInteractor.getAllAlbums();
    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {
        Navigator.navigateToVKAlbumFragment(context, photoAlbum);
    }

    @Override
    public void addAlbum(final String title, final String description,
                         final int privacy, final int commentPrivacy) {
        vkAlbumsInteractor.addAlbum(title, description, privacy, commentPrivacy);
    }


    @Override
    public void deleteVkAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.deleteVkAlbum(multiSelector, vkAlbumsView.getAdapterCursor());
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
    public void onGetVkSaveAlbumEvent(GetVkSaveAlbumEvent getVkSaveAlbumEvent) {
        vkAlbumsView.displayVkSaveAlbum(getVkSaveAlbumEvent.photoAlbum);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnLocalAlbumEvent(LocalAlbumEvent localAlbumEvent) {
        vkAlbumsView.displayVkAlbums();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddAlbumEvent(GetAlbumEvent getAlbumEvent) {
        vKAddAlbumView.displayVkAddAlbum(getAlbumEvent.photoAlbum);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkAlbumsView.showError(errorEvent.errorMessage);
    }
}
      