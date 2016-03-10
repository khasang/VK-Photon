package com.khasang.vkphoto.presentation.model;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

import com.khasang.vkphoto.R;

public class MyActionExpandListener implements MenuItemCompat.OnActionExpandListener {
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }
}
