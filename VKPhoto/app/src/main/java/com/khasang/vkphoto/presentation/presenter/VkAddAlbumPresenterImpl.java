package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;

import com.khasang.vkphoto.domain.events.GetAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkAddAlbumInteractor;
import com.khasang.vkphoto.domain.interactors.VkAddAlbumInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAddAlbumView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/** Created by bugtsa on 19-Feb-16. */
public class VkAddAlbumPresenterImpl implements VkAddAlbumPresenter {
    private VkAddAlbumView vkAddAlbumView;
    private VkAddAlbumInteractor vkAddAlbumInteractor;

    public VkAddAlbumPresenterImpl(VkAddAlbumView vkAddAlbumView, SyncServiceProvider syncServiceProvider) {
        this.vkAddAlbumView = vkAddAlbumView;
        vkAddAlbumInteractor = new VkAddAlbumInteractorImpl(syncServiceProvider);
    }

    @Override
    public void addAlbum(final String title, final String description,
                         final int privacy, final int commentPrivacy) {
        vkAddAlbumInteractor.addAlbum(title, description, privacy, commentPrivacy);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddAlbumEvent(GetAlbumEvent getAlbumEvent) {
        vkAddAlbumView.displayVkAddAlbum(getAlbumEvent.photoAlbum);
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

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {
        Navigator.navigateToVKAlbumFragment(context, photoAlbum);
    }
}
