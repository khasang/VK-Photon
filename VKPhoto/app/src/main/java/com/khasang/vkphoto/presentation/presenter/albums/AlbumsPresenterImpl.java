package com.khasang.vkphoto.presentation.presenter.albums;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
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
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.NetWorkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        actionMode.finish();
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

    @Override
    public void editAlbumById(int albumId, String title, String description) {
        vkAlbumsInteractor.editAlbum(albumId, title, description);
    }

    @Override
    public void editPrivacyAlbumById(int albumId, int privacy) {
        Logger.d(String.valueOf(privacy));
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
    public void hideActionModeItem(MultiSelector multiSelector, MenuItem menuItem) {
        MenuItem itemActionEditAlbum = actionMode.getMenu().findItem(R.id.action_edit_album);
        super.hideActionModeItem(multiSelector, itemActionEditAlbum);
    }

    @Override
    public void selectAlbum(final MultiSelector multiSelector, final AppCompatActivity activity) {
        this.actionMode = activity.startSupportActionMode(new MyActionModeCallback(multiSelector, activity,
                R.menu.menu_action_mode_vk_albums, ((FabProvider) activity).getFloatingActionButton()) {
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                checkShowCancelSync(multiSelector, menu);
                return super.onPrepareActionMode(actionMode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sync_album:
                        vkAlbumsView.confirmSync(multiSelector);
                        return true;
                    case R.id.action_download_album:
                        return true;
                    case R.id.action_edit_album:
                        editSelectedAlbum(multiSelector);
                        return true;
                    case R.id.action_delete_album:
                        vkAlbumsView.confirmDelete(multiSelector);
                        return true;
                    case R.id.action_cancel_sync_album:
                        vkAlbumsInteractor.cancelAlbumsSync(getSelectedAlbums(multiSelector.getSelectedPositions(), vkAlbumsView.getAdapterCursor()));
                        return true;
                    case R.id.action_privacy:
                        editPrivacySelectedAlbum(multiSelector);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void editPrivacySelectedAlbum(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Cursor cursor = vkAlbumsView.getAdapterCursor();
        PhotoAlbum album;
        if (cursor != null) {
            Integer position = selectedPositions.get(0);
            cursor.moveToPosition(position);
            album = new PhotoAlbum(cursor);

            vkAlbumsView.editPrivacy(album.getId(), album.privacy);
        }
    }

    private void editSelectedAlbum(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Cursor cursor = vkAlbumsView.getAdapterCursor();
        PhotoAlbum album;
        if (cursor != null) {
            Integer position = selectedPositions.get(0);
            cursor.moveToPosition(position);
            album = new PhotoAlbum(cursor);
            vkAlbumsView.editAlbum(album.getId(), album.title, album.description);
        }
    }

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector) {
        super.checkActionModeFinish(multiSelector);
        checkShowCancelSync(multiSelector, actionMode.getMenu());
    }

    private void checkShowCancelSync(MultiSelector multiSelector, Menu menu) {
        List<Integer> indexes = multiSelector.getSelectedPositions();
        Cursor cursor = vkAlbumsView.getAdapterCursor();
        List<PhotoAlbum> photoAlbumList = getSelectedAlbums(indexes, cursor);
        for (PhotoAlbum photoAlbum : photoAlbumList) {
            Logger.d("" + photoAlbum + photoAlbum.syncStatus);
            if (photoAlbum.syncStatus == Constants.SYNC_STARTED) {
                menu.findItem(R.id.action_cancel_sync_album).setVisible(true);
                return;
            }
        }
    }

    private List<PhotoAlbum> getSelectedAlbums(List<Integer> indexes, Cursor cursor) {
        List<PhotoAlbum> photoAlbumList = new ArrayList<>();
        for (int i = 0; i < indexes.size(); i++) {
            cursor.moveToPosition(indexes.get(i));
            photoAlbumList.add(new PhotoAlbum(cursor));
        }
        return photoAlbumList;
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
      