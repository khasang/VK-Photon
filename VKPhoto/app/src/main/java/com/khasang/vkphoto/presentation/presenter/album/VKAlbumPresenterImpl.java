package com.khasang.vkphoto.presentation.presenter.album;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.callbacks.MyActionModeCallback;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetLocalAlbumsEvent;
import com.khasang.vkphoto.domain.events.GetSynchronizedPhotosEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotoEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.events.GetSwipeRefreshEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interactors.VKAlbumInteractor;
import com.khasang.vkphoto.domain.interactors.VKAlbumInteractorImpl;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VKAlbumPresenterImpl extends AlbumPresenterBase implements VKAlbumPresenter {
    private AlbumView vkAlbumView;
    private VKAlbumInteractor VKAlbumInteractor;
    private boolean onGetSynchronizedPhotosEventCaught = false;
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
        Logger.d("VKAlbumPresenterImpl onGetSynchronizedPhotosEvent");
        EventBus.getDefault().removeStickyEvent(GetSynchronizedPhotosEvent.class);
        vkAlbumView.displayVkPhotos(getSynchronizedPhotosEvent.photosList);
        onGetSynchronizedPhotosEventCaught = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSwipeRefreshEvent(GetSwipeRefreshEvent getSwipeRefreshEvent) {
        vkAlbumView.displayRefresh(getSwipeRefreshEvent.refreshing);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKPhotoEvent(GetVKPhotoEvent event){
        vkAlbumView.displayRefresh(true);
        List<Photo> albumPhotoList = vkAlbumView.getPhotoList();
        albumPhotoList.add(event.photo);
        vkAlbumView.displayVkPhotos(albumPhotoList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        Logger.d("VKAlbumPresenterImpl onGetVKPhotosEvent");
        if (!onGetSynchronizedPhotosEventCaught) {
            Logger.d("VKAlbumPresenterImpl SynchronizedPhotos not loaded yet. Will try to sleep for 0.5 sec");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            EventBus.getDefault().removeStickyEvent(GetVKPhotosEvent.class);
            List<Photo> synchronizedPhotos = vkAlbumView.getPhotoList();
            List<Photo> receivedFromVKPhotos = getVKPhotosEvent.photosList;
            List<Photo> deleteListFromLocal = new ArrayList<>();
            int added = 0, removed = 0;
            Logger.d("VKAlbumPresenterImpl synchronizedPhotos.size=" + synchronizedPhotos.size());
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
//                    synchronizedPhotos.remove(addedPhoto); //ConcurrentModificationException
                    removed++;
                }
            }

            Logger.d("VKAlbumPresenterImpl added=" + added + ", removed=" + removed + " photos");
            //выводим на экран результирующий лист
            vkAlbumView.displayVkPhotos(synchronizedPhotos);
        }
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
