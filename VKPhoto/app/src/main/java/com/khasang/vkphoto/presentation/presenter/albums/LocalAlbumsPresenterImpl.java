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
import com.khasang.vkphoto.domain.interactors.LocalAlbumsInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class LocalAlbumsPresenterImpl extends AlbumsPresenterBase implements LocalAlbumsPresenter {
    private VkAlbumsView albumsView;
    private LocalAlbumsInteractorImpl albumsInteractor;
    private ActionMode actionMode;

    public LocalAlbumsPresenterImpl(VkAlbumsView albumsView, Context context) {
        this.albumsView = albumsView;
        albumsInteractor = new LocalAlbumsInteractorImpl(context);
    }

    @Override
    public void addAlbum(String title, String thumbPath) {
        //TODO: implement metod
        Logger.d("user wants to add new local album");
        Logger.d("no body");
//        albumsInteractor.addAlbum(title, description, privacy, commentPrivacy);
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
    public void deleteAlbums(MultiSelector multiSelector, Context context) {
        albumsInteractor.deleteSelectedLocalAlbums(multiSelector, albumsView.getAdapterCursor(), context);
        albumsView.displayAlbums();
        actionMode.finish();
    }


    //TODO: понятия не имею, что такое @Subscribe. код ниже может быть не работоспособен
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVkSaveAlbumEvent(GetVkSaveAlbumEvent getVkSaveAlbumEvent) {
//        albumsView.displayVkSaveAlbum(getVkSaveAlbumEvent.photoAlbum);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnLocalAlbumEvent(LocalAlbumEvent localAlbumEvent) {
//        albumsView.displayAlbums();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
//        albumsView.showError(errorEvent.errorMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncAndTokenReadyEvent(SyncAndTokenReadyEvent syncAndTokenReadyEvent) {
//        getAllVKAlbums();
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
                    case R.id.action_edit_album:
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

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector) {
        if (multiSelector.getSelectedPositions().size() == 0) {
            if (actionMode != null) {
                actionMode.finish();
            }
        }
    }

    @Override
    public void exportAlbums(MultiSelector multiSelector) {
        Logger.d("user wants to export local albums");
    }
}
