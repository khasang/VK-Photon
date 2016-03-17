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
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.events.VKAlbumEvent;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractor;
import com.khasang.vkphoto.domain.interactors.VkAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.AlbumsView;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.NetWorkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class AlbumsPresenterImpl extends AlbumsPresenterBase implements VKAlbumsPresenter {
    private AlbumsView vkAlbumsView;
    private VkAlbumsInteractor vkAlbumsInteractor;

    public AlbumsPresenterImpl(AlbumsView vkAlbumsView, SyncServiceProvider syncServiceProvider) {
        this.vkAlbumsView = vkAlbumsView;
        vkAlbumsInteractor = new VkAlbumsInteractorImpl(syncServiceProvider);
    }

    public void syncAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.syncAlbums(multiSelector, vkAlbumsView.getAdapterCursor());
    }

    @Override
    public void getAllVKAlbums() {
        if (NetWorkUtils.isNetworkOnline(vkAlbumsView.getContext())) {
            vkAlbumsInteractor.getAllAlbums();
        } else {
            EventBus.getDefault().postSticky(new ErrorEvent(ErrorUtils.NO_INTERNET_CONNECTION_ERROR));
        }
    }

    @Override
    public void exportAlbums(MultiSelector multiSelector) {

    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {
        Navigator.navigateToVKAlbumFragment(vkAlbumsView.getContext(), photoAlbum);
    }

    @Override
    public void addAlbum(String title, String description, int privacy, int commentPrivacy) {
        vkAlbumsInteractor.addAlbum(title, description, privacy, commentPrivacy);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnVKAlbumEvent(VKAlbumEvent VKAlbumEvent) {
        Logger.d("got vkAlbumEvent");
        vkAlbumsView.displayAlbums();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
        vkAlbumsView.showError(errorEvent.errorCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncAndTokenReadyEvent(SyncAndTokenReadyEvent syncAndTokenReadyEvent) {
        EventBus.getDefault().removeStickyEvent(SyncAndTokenReadyEvent.class);
        vkAlbumsView.displayRefresh(true);
        getAllVKAlbums();
    }

    @Override
    public void onStart() {
        super.onStart();
        SyncAndTokenReadyEvent stickyEvent = EventBus.getDefault().removeStickyEvent(SyncAndTokenReadyEvent.class);
        if (stickyEvent != null) {
            onSyncAndTokenReadyEvent(stickyEvent);
        }
    }

    @Override
    public void selectAlbum(final MultiSelector multiSelector, final AppCompatActivity activity) {
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity,
                R.menu.menu_action_mode_vk_albums, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_album:
                        vkAlbumsView.confirmSync(multiSelector);
                        return true;
                    case R.id.action_edit_album:
                        return true;
                    case R.id.action_delete_album:
                        vkAlbumsView.confirmDelete(multiSelector);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public File getAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor) {
        return photoAlbum.thumb_id > 0 ? vkAlbumsInteractor.downloadAlbumThumb(localPhotoSource, photoAlbum, executor) : null;
    }

    @Override
    public void deleteSelectedAlbums(MultiSelector multiSelector) {
        vkAlbumsInteractor.deleteVkAlbum(multiSelector, vkAlbumsView.getAdapterCursor());
        actionMode.finish();
    }
}
      