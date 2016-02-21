package com.khasang.vkphoto.presentation.view;

import android.app.Activity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.util.Logger;

public class ActionModeVKAlbumsCallback extends ModalMultiSelectorCallback {
    private MenuInflater menuInflater;
    private VKAlbumsPresenter vkAlbumsPresenter;
    private MultiSelector multiSelector;

    public ActionModeVKAlbumsCallback(MultiSelector multiSelector, Activity activity, VKAlbumsPresenter vkAlbumsPresenter) {
        super(multiSelector);
        this.vkAlbumsPresenter = vkAlbumsPresenter;
        this.menuInflater = activity.getMenuInflater();
        this.multiSelector = multiSelector;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        super.onCreateActionMode(actionMode, menu);
        menuInflater.inflate(R.menu.action_mode_vk_albums, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                // Delete crimes from model
                Logger.d("settings pressed");
                multiSelector.clearSelections();
                return true;
            default:
                break;
        }
        return false;
    }
}
