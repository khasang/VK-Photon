package com.khasang.vkphoto.presentation.presenter;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAlbumView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class VKAlbumPresenterImpl implements VKPhotosPresenter {
    private VkAlbumView vkAlbumView;
    private VkPhotosInteractor vkPhotosInteractor;

    public VKAlbumPresenterImpl(VkAlbumView vkAlbumView, SyncServiceProvider syncServiceProvider) {
        this.vkAlbumView = vkAlbumView;
        vkPhotosInteractor = new VkPhotosInteractorImpl(syncServiceProvider);
    }


    @Override
    public void getPhotosByAlbumId(int albumId) {
        vkPhotosInteractor.getPhotosByAlbumId(albumId);
    }

    @Override
    public void deletePhotoById(int photoId) {
        vkPhotosInteractor.deletePhotoById(photoId);
    }

    @Override
    public void addPhotos(ArrayList<String> listUploadedFiles, PhotoAlbum photoAlbum) {
        vkPhotosInteractor.addPhotos(listUploadedFiles, photoAlbum);
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
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkAlbumView.showError(errorEvent.errorMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        vkAlbumView.displayVkPhotos(getVKPhotosEvent.photosList);
    }
}
