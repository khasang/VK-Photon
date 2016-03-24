package com.khasang.vkphoto.presentation.presenter.albums;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.LocalALbumEvent;
import com.khasang.vkphoto.domain.interactors.LocalAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.AlbumsView;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class LocalAlbumsPresenterImpl extends AlbumsPresenterBase implements LocalAlbumsPresenter {
    private AlbumsView albumsView;
    private LocalAlbumsInteractorImpl albumsInteractor;

    public LocalAlbumsPresenterImpl(AlbumsView albumsView, SyncServiceProvider syncServiceProvider) {
        this.albumsView = albumsView;
        albumsInteractor = new LocalAlbumsInteractorImpl(syncServiceProvider);
    }

    @Override
    public void addAlbum(String title) {
        albumsInteractor.addAlbum(title);
    }

    public void syncAlbums(MultiSelector multiSelector) {
        Logger.d("user wants to sync local albums list");
        albumsInteractor.syncLocalAlbums(multiSelector, albumsView.getAdapterCursor());
    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum) {
        Navigator.navigateToLocalAlbumFragment(context, photoAlbum);
    }

    @Override
    public File getAlbumThumb(LocalPhotoSource localPhotoSource, PhotoAlbum photoAlbum, ExecutorService executor) {
        return new File(photoAlbum.thumbFilePath);
    }

    @Override
    public void deleteSelectedAlbums(MultiSelector multiSelector) {
        albumsInteractor.deleteLocalAlbums(multiSelector, albumsView.getAdapterCursor());
//        albumsView.removeAlbumsFromView();
        actionMode.finish();
    }

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector) {
        super.checkActionModeFinish(multiSelector);
    }

    @Override
    public void hideActionModeItem(MultiSelector multiSelector, MenuItem menuItem) {
        MenuItem itemActionEditAlbum = actionMode.getMenu().findItem(R.id.action_edit_album);
        super.hideActionModeItem(multiSelector, itemActionEditAlbum);
    }

    @Override
    public void selectAlbum(final MultiSelector multiSelector, final AppCompatActivity activity) {
        this.actionMode = activity.startSupportActionMode(
                new MyActionModeCallback(multiSelector, activity, R.menu.menu_action_mode_local_albums,
                        ((FabProvider) activity).getFloatingActionButton()) {
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_sync_album:
                                syncAlbums(multiSelector);
                                return true;
//                            case R.id.action_upload_album:
//                                return true;
                            case R.id.action_edit_album:
                                editSelectedAlbum(multiSelector);
                                return true;
                            case R.id.action_select_all:
                                for (int i = 0; i < albumsView.getAdapterCursor().getCount(); i++) {
                                    multiSelector.setSelected(i, 0, true);
                                    actionMode.getMenu().findItem(R.id.action_edit_album).setVisible(false);
                                }
                                return true;
                            case R.id.action_delete_album:
                                albumsView.confirmDelete(multiSelector);
                                return true;
                            default:
                                break;
                        }
                        return false;
                    }
                });
    }

    private void editSelectedAlbum(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Cursor cursor = albumsView.getAdapterCursor();
        PhotoAlbum album;
        if (cursor != null) {
            Integer position = selectedPositions.get(0);
            cursor.moveToPosition(position);
            album = new PhotoAlbum(cursor);
            albumsView.editAlbum(album.getId(), album.title, null);
        }
    }

    @Override
    public void editAlbumById(int albumId, String title) {
        albumsInteractor.editAlbum(albumId, title);
        actionMode.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
//        vkAlbumsView.showError(errorEvent.errorCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocalAlbumEvent(LocalALbumEvent errorEvent) {
        albumsView.displayAlbums();
    }

    @Override
    public List<PhotoAlbum> getAllLocalAlbums() {
        return albumsInteractor.getAllLocalAlbums();
    }

    @Override
    public void exportAlbums(MultiSelector multiSelector) {
        Logger.d("user wants to export local albums");
    }
}
