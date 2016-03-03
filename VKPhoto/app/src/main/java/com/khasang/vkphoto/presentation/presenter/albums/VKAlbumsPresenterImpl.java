package com.khasang.vkphoto.presentation.presenter.albums;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVkSaveAlbumEvent;
import com.khasang.vkphoto.domain.events.LocalAlbumEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class VKAlbumsPresenterImpl extends AlbumsPresenterBase implements VKAlbumsPresenter {
    private VkAlbumsView vkAlbumsView;
    private VkAlbumsInteractor vkAlbumsInteractor;
    private ActionMode actionMode;

    public VKAlbumsPresenterImpl(VkAlbumsView vkAlbumsView, SyncServiceProvider syncServiceProvider) {
        this.vkAlbumsView = vkAlbumsView;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider);
    }

    public void syncAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.syncAlbums(multiSelector, vkAlbumsView.getAdapterCursor());
    }

    @Override
    public void getAllAlbums() {
        vkAlbumsInteractor.getAllAlbums();
    }

    @Override
    public void exportAlbums(MultiSelector multiSelector) {

    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {
        Navigator.navigateToVKAlbumFragment(context, photoAlbum);
    }

    @Override
    public void addAlbum(String title, String description, int privacy, int commentPrivacy) {
        vkAlbumsInteractor.addAlbum(title, description, privacy, commentPrivacy);
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
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkAlbumsView.showError(errorEvent.errorMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncAndTokenReadyEvent(SyncAndTokenReadyEvent syncAndTokenReadyEvent) {
        getAllAlbums();
    }

    @Override
    public void selectAlbum(final MultiSelector multiSelector, final AppCompatActivity activity) {
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity, R.menu.menu_action_mode_vk_albums, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_album:
                        syncAlbums(multiSelector);
                        return true;
                    case R.id.action_delete_album:
                        vkAlbumsView.confirmDelete(multiSelector);
//                            vkAlbumsPresenter.deleteVkAlbums(multiSelector);
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
            if (actionMode != null) {
                actionMode.finish();
            }
        }
    }

    public File getAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor) {
        return photoAlbum.thumb_id > 0 ? vkAlbumsInteractor.downloadAlbumThumb(localPhotoSource, photoAlbum, executor) : null;
    }

    @Override
    public void deleteAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.deleteVkAlbum(multiSelector, vkAlbumsView.getAdapterCursor());
    }
}
      