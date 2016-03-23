package com.khasang.vkphoto.presentation.presenter.album;

import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.MultiSelector;

import org.greenrobot.eventbus.EventBus;

public abstract class AlbumPresenterBase implements AlbumPresenter {
    protected ActionMode actionMode;

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    public void checkActionModeFinish(MultiSelector multiSelector) {
        int size = multiSelector.getSelectedPositions().size();
        if (size == 0) {
            if (actionMode != null) {
                actionMode.finish();
            }
        }
    }

    public void hideActionModeItem(MultiSelector multiSelector, MenuItem menuItem) {
        int size = multiSelector.getSelectedPositions().size();
        if (size == 1) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
    }

    @Override
    public void initialize() {

    }
}
