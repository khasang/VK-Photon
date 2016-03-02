package com.khasang.vkphoto.domain.callbacks;

import android.app.Activity;
import android.support.annotation.MenuRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;

import java.lang.ref.WeakReference;

public class MyActionModeCallback extends ModalMultiSelectorCallback {
    private MultiSelector multiSelector;
    private WeakReference<Activity> activityWeakReference;
    private int menuRes;
    private FloatingActionButton floatingActionButton;

    public MyActionModeCallback(MultiSelector multiSelector, Activity activity, @MenuRes int menuRes) {
        super(multiSelector);
        this.multiSelector = multiSelector;
        this.activityWeakReference = new WeakReference<>(activity);
        this.menuRes = menuRes;
    }

    public MyActionModeCallback(MultiSelector multiSelector, AppCompatActivity activity, @MenuRes int menuRes, FloatingActionButton floatingActionButton) {
        this(multiSelector, activity, menuRes);
        this.floatingActionButton = floatingActionButton;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        Activity activity = activityWeakReference.get();
        if (activity != null) {
            activity.getMenuInflater().inflate(menuRes, menu);
            if (floatingActionButton != null) {
                floatingActionButton.hide();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        multiSelector.clearSelections();
        if (floatingActionButton != null) {
            floatingActionButton.show();
        }
        super.onDestroyActionMode(actionMode);
    }
}
