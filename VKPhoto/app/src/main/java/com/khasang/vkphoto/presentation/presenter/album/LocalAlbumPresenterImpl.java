package com.khasang.vkphoto.presentation.presenter.album;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalPhotosEvent;
import com.khasang.vkphoto.domain.interactors.LocalPhotosInteractor;
import com.khasang.vkphoto.domain.interactors.LocalPhotosInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalAlbumPresenterImpl  extends AlbumPresenterBase implements LocalAlbumPresenter {
    private AlbumView albumView;
    private LocalPhotosInteractor localPhotosInteractor;
//    private ActionMode actionMode;

    public LocalAlbumPresenterImpl(AlbumView vkAlbumView, SyncServiceProvider syncServiceProvider) {
        this.albumView = vkAlbumView;
        localPhotosInteractor = new LocalPhotosInteractorImpl(syncServiceProvider);
    }

    @Override
    public void selectPhoto(final MultiSelector multiSelector, final AppCompatActivity activity) {
        ((FabProvider) activity).getFloatingActionButton().hide();
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity,
                R.menu.menu_action_mode_local_album, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_photo:
                        Logger.d("user wants to sync local photos");
                        return true;
                    case R.id.action_edit_photo:
                        return true;
                    case R.id.action_delete_photo:
                        albumView.confirmDelete(multiSelector);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void savePhotos(final MultiSelector multiSelector, final long idVKPhotoAlbum, final AppCompatActivity activity) {
        ((FabProvider) activity).getFloatingActionButton().hide();
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity,
                R.menu.menu_action_mode_save_photos, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_select_all:
                        Logger.d("user wants save all local photos");
                        return true;
                    case R.id.action_save_photos:
                        localPhotosInteractor.savePhotos(multiSelector, idVKPhotoAlbum);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector) {
        if (multiSelector.getSelectedPositions().size() == 0) {
            if (actionMode != null) actionMode.finish();
        }
    }

    @Override
    public void getLocalAlbumsCursor() {}

    @Override
    public void getPhotosByAlbumId(int albumId) {
        localPhotosInteractor.getPhotosByAlbumId(albumId);
    }

    @Override
    public void deleteSelectedPhotos(MultiSelector multiSelector) {
        localPhotosInteractor.deleteSelectedLocalPhotos(multiSelector, albumView.getPhotoList());
        albumView.removePhotosFromView();
        actionMode.finish();
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
//        albumView.showError(errorEvent.errorCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetLocalPhotosEvent(GetLocalPhotosEvent getLocalPhotosEvent) {
        albumView.displayVkPhotos(getLocalPhotosEvent.photosList);
    }
}
