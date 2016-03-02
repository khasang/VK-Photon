package com.khasang.vkphoto.presentation.presenter.albums;

import android.support.v7.view.ActionMode;

import com.bignerdranch.android.multiselector.MultiSelector;

import org.greenrobot.eventbus.EventBus;

public abstract class AlbumsPresenterBase implements AlbumsPresenter {
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

    @Override
    public void initialize() {

    }
}
