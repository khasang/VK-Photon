package com.khasang.vkphoto.presentation.presenter;

import android.content.Context;
import android.support.v7.view.ActionMode;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import org.greenrobot.eventbus.EventBus;

public abstract class AlbumsPresenter implements Presenter {
    private ActionMode actionMode;

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    public void checkActionModeFinish(MultiSelector multiSelector) {
        if (multiSelector.getSelectedPositions().size() == 0) {
            if (actionMode != null) {
                actionMode.finish();
            }
        }
    }

    abstract void goToPhotoAlbum(Context context, PhotoAlbum photoAlbum);
}
