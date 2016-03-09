package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.interactors.LocalPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.LocalPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.VkPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalAlbumPresenterImpl implements LocalAlbumPresenter {
    private VkAlbumView vkAlbumView;
    private LocalPhotosInteractor localPhotosInteractor;
    private ActionMode actionMode;

    public LocalAlbumPresenterImpl(VkAlbumView vkAlbumView) {
        this.vkAlbumView = vkAlbumView;
        localPhotosInteractor = new LocalPhotosInteractorImpl();
    }


    @Override
    public List<Photo> getPhotosByAlbum(PhotoAlbum photoAlbum, Context context) {
        return localPhotosInteractor.getPhotosByAlbum(photoAlbum, context);
    }

    @Override
    public void selectPhoto(final MultiSelector multiSelector, final AppCompatActivity activity) {
        ((FabProvider) activity).getFloatingActionButton().hide();
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity, R.menu.menu_action_mode_vk_album, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_photo:
                        Logger.d("user wants to sync local photos");
                        return true;
                    case R.id.action_delete_photo:
                        deleteSelectedLocalPhotos(multiSelector);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector, Context context) {
        if (multiSelector.getSelectedPositions().size() == 0) {
            ((FabProvider) context).getFloatingActionButton().show();
            if (actionMode != null) actionMode.finish();
        }
    }

    @Override
    public void deleteSelectedLocalPhotos(MultiSelector multiSelector) {
        Logger.d("user wants to deleteSelectedLocalPhotos");
//        vkPhotosInteractor.deleteSelectedVkPhotos(multiSelector, vkAlbumView.getPhotoList());
    }


    //Presenter implementations
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