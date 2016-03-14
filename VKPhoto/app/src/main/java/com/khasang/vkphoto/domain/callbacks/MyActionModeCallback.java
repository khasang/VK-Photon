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
import com.khasang.vkphoto.domain.events.CloseActionModeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class MyActionModeCallback extends ModalMultiSelectorCallback {
    private MultiSelector multiSelector;
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ActionMode> actionModeWeakReference;
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
        actionModeWeakReference = new WeakReference<>(actionMode);
        if (activity != null) {
            activity.getMenuInflater().inflate(menuRes, menu);
            if (floatingActionButton != null) {
                floatingActionButton.hide();
            }
            EventBus.getDefault().register(this);
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseActionModeEvent(CloseActionModeEvent closeActionModeEvent) {
        multiSelector.clearSelections();
        ActionMode actionMode = actionModeWeakReference.get();
        if (actionMode != null) {
            actionMode.finish();
        }
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
        EventBus.getDefault().unregister(this);
        super.onDestroyActionMode(actionMode);
    }
}
