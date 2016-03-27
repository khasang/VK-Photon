package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalAlbumsEvent;
import com.khasang.vkphoto.domain.events.GetSwipeRefreshEvent;
import com.khasang.vkphoto.domain.events.GetSynchronizedPhotosEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotoEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interactors.AlbumInteractor;
import com.khasang.vkphoto.domain.interactors.AlbumInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlbumPresenterImpl extends AlbumPresenterBase implements VKAlbumPresenter {
    private AlbumView vkAlbumView;
    private AlbumInteractor albumInteractor;
    private boolean onGetSynchronizedPhotosEventCaught = false;
    private PhotoAlbum photoAlbum;
    private Handler handler;

    public AlbumPresenterImpl(AlbumView vkAlbumView, SyncServiceProvider syncServiceProvider, PhotoAlbum photoAlbum) {
        this.vkAlbumView = vkAlbumView;
        this.photoAlbum = photoAlbum;
        albumInteractor = new AlbumInteractorImpl(syncServiceProvider);
        handler = new Handler();
    }


    @Override
    public void getPhotosByAlbumId(int albumId) {
        albumInteractor.getPhotosByAlbumId(albumId);
    }

    @Override
    public void uploadPhotos(MultiSelector multiSelector, final long idPhotoAlbum, final AppCompatActivity activity) {
    }

    @Override
    public void deleteSelectedPhotos(MultiSelector multiSelector) {
        List<Photo> photoList = new ArrayList<>(vkAlbumView.getPhotoList());
        vkAlbumView.removePhotosFromView();
        albumInteractor.deleteSelectedVkPhotos(multiSelector, photoList);
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void getAllLocalAlbums() {
        albumInteractor.getAllLocalAlbums();
    }

    @Override
    public void goToPhotoAlbum(Context context, PhotoAlbum selectedLocalPhotoAlbum, PhotoAlbum vkAlbum) {
        Navigator.navigateToLocalAlbumFragmentWithReplace(context, selectedLocalPhotoAlbum, vkAlbum);
    }

    @Override
    public void syncPhotos(MultiSelector multiSelector) {
        albumInteractor.syncPhotos(multiSelector, new ArrayList<>(vkAlbumView.getPhotoList()), photoAlbum);
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        onGetSynchronizedPhotosEventCaught = false;
        GetSwipeRefreshEvent getSwipeRefreshEvent = EventBus.getDefault().removeStickyEvent(GetSwipeRefreshEvent.class);
        if (getSwipeRefreshEvent != null) {
            onGetSwipeRefreshEvent(getSwipeRefreshEvent);
        }
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
    public void onGetSynchronizedPhotosEvent(GetSynchronizedPhotosEvent getSynchronizedPhotosEvent) {
        Logger.d("AlbumPresenterImpl onGetSynchronizedPhotosEvent");
        EventBus.getDefault().removeStickyEvent(GetSynchronizedPhotosEvent.class);
        vkAlbumView.displayPhotos(getSynchronizedPhotosEvent.photosList);
        onGetSynchronizedPhotosEventCaught = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSwipeRefreshEvent(GetSwipeRefreshEvent getSwipeRefreshEvent) {
        vkAlbumView.displayRefresh(getSwipeRefreshEvent.refreshing);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKPhotoEvent(GetVKPhotoEvent event) {
        vkAlbumView.displayRefresh(true);
        List<Photo> albumPhotoList = new ArrayList<>(vkAlbumView.getPhotoList());
        albumPhotoList.add(0, event.photo);
        if (photoAlbum.syncStatus != Constants.SYNC_NOT_STARTED) {
            new LocalPhotoSource(vkAlbumView.getContext()).savePhotoToAlbum(event.photo, photoAlbum);
        }
        updateView(albumPhotoList);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        Logger.d("AlbumPresenterImpl onGetVKPhotosEvent");
        if (!onGetSynchronizedPhotosEventCaught) {
            Logger.d("AlbumPresenterImpl SynchronizedPhotos not loaded yet. Will try to sleep for 0.5 sec");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            EventBus.getDefault().removeStickyEvent(GetVKPhotosEvent.class);
            final List<Photo> synchronizedPhotos = new ArrayList<>(vkAlbumView.getPhotoList());
            List<Photo> receivedFromVKPhotos = getVKPhotosEvent.photosList;
            List<Photo> deleteListFromLocal = new ArrayList<>();
            int added = 0, removed = 0;
            Logger.d("AlbumPresenterImpl synchronizedPhotos.size=" + synchronizedPhotos.size());
            //сначала добавляем в лист фото, которые еще не синхонизировались
            for (Photo vkPhoto : receivedFromVKPhotos) {
                if (!synchronizedPhotos.contains(vkPhoto)) {
                    synchronizedPhotos.add(vkPhoto);
                    added++;
                }
            }
            //потом убираем из листа фото, синхронизировавшиеся ранее и удаленные из вк
            Iterator<Photo> iter = synchronizedPhotos.iterator();
            while (iter.hasNext()) {
                Photo addedPhoto = iter.next();
                if (!receivedFromVKPhotos.contains(addedPhoto)) {
                    deleteListFromLocal.add(addedPhoto);
                    iter.remove();
                    removed++;
                }
            }
            if (photoAlbum.syncStatus != Constants.SYNC_NOT_STARTED) {
                new LocalPhotoSource(vkAlbumView.getContext()).deletePhotoListFromDB(deleteListFromLocal);
            }
            Logger.d("AlbumPresenterImpl added=" + added + ", removed=" + removed + " photos");
            EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
            //выводим на экран результирующий лист
            updateView(synchronizedPhotos);
        }
    }

    private void updateView(final List<Photo> synchronizedPhotos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                vkAlbumView.displayPhotos(synchronizedPhotos);
            }
        });
    }

    @Override
    public void checkActionModeFinish(MultiSelector multiSelector) {
        super.checkActionModeFinish(multiSelector);
    }

    @Override
    public void hideActionModeItem(MultiSelector multiSelector, MenuItem menuItem) {
        MenuItem itemActionEditPhoto = actionMode.getMenu().findItem(R.id.action_edit_photo);
        MenuItem itemDownLoadPhoto = actionMode.getMenu().findItem(R.id.action_download_photo);
        super.hideActionModeItem(multiSelector, itemActionEditPhoto);
//        super.hideActionModeItem(multiSelector, itemDownLoadPhoto);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKAlbumsEvent(GetLocalAlbumsEvent getLocalAlbumsEvent) {
        vkAlbumView.displayAllLocalAlbums(getLocalAlbumsEvent.albumsList);
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
                        vkAlbumView.confirmSync();
                        return true;
                    case R.id.action_download_photo:
                        return true;
                    case R.id.action_edit_photo:
                        return true;
                    case R.id.action_select_all:
                        for (int i = 0; i < vkAlbumView.getPhotoList().size(); i++) {
                            multiSelector.setSelected(i, 0, true);
                            actionMode.getMenu().findItem(R.id.action_download_photo).setVisible(false);
                            actionMode.getMenu().findItem(R.id.action_edit_photo).setVisible(false);
                        }
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
