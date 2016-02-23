package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.domain.events.LocalAddAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkAddAlbumInteractor;
import com.khasang.vkphoto.domain.interactors.VkAddAlbumInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.view.VkAddAlbumView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Vector;

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
                         final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy) {
        vkAddAlbumInteractor.addAlbum(title, description, listUploadedFiles, privacy, comment_privacy);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddAlbumEvent(LocalAddAlbumEvent localAddAlbumEvent) {
        vkAddAlbumView.displayVkAddAlbum();
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
}
