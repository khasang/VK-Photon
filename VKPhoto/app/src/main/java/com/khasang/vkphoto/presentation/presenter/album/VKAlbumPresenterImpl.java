package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalAlbumstEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.interactors.VKAlbumInteractor;
import com.khasang.vkphoto.domain.interactors.VKAlbumInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.AlbumView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class VKAlbumPresenterImpl extends AlbumPresenterBase implements VKAlbumPresenter {
    private AlbumView vkAlbumView;
    private VKAlbumInteractor VKAlbumInteractor;
//    private ActionMode actionMode;

    public VKAlbumPresenterImpl(AlbumView vkAlbumView, SyncServiceProvider syncServiceProvider) {
        this.vkAlbumView = vkAlbumView;
        VKAlbumInteractor = new VKAlbumInteractorImpl(syncServiceProvider);
    }


    @Override
    public void getPhotosByAlbumId(int albumId) {
        VKAlbumInteractor.getPhotosByAlbumId(albumId);
    }

    @Override
    public void uploadPhotos(MultiSelector multiSelector, final long idPhotoAlbum, final AppCompatActivity activity) {}

    @Override
    public void deleteSelectedPhotos(MultiSelector multiSelector) {
        VKAlbumInteractor.deleteSelectedVkPhotos(multiSelector, vkAlbumView.getPhotoList());
        vkAlbumView.removePhotosFromView();
        actionMode.finish();
    }

    @Override
    public void getAllLocalAlbums() {
        VKAlbumInteractor.getAllLocalAlbums();
    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum selectedLocalPhotoAlbum, long idVKPhotoAlbum) {
        Navigator.navigateToLocalAlbumFragmentWithReplace(context, selectedLocalPhotoAlbum, idVKPhotoAlbum);
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
        vkAlbumView.showError(errorEvent.errorCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        vkAlbumView.displayVkPhotos(getVKPhotosEvent.photosList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKAlbumsEvent(GetLocalAlbumstEvent getLocalAlbumstEvent) {
        vkAlbumView.displayAllLocalAlbums(getLocalAlbumstEvent.albumsList);
    }

    @Override
    public void selectPhoto(final MultiSelector multiSelector, final AppCompatActivity activity) {
        ((FabProvider) activity).getFloatingActionButton().hide();
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity,
                R.menu.menu_action_mode_vk_album, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_photo:
                        return true;
                    case R.id.action_edit_photo:
                        return true;
                    case R.id.action_delete_photo:
                        vkAlbumView.confirmDelete(multiSelector);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
