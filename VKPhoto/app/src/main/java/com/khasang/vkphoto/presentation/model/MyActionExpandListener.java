package com.khasang.vkphoto.presentation.model;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

public class MyActionExpandListener implements MenuItemCompat.OnActionExpandListener {
    private MenuItem microMenuItem;

    public MyActionExpandListener(MenuItem microMenuItem) {
        this.microMenuItem = microMenuItem;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        microMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        microMenuItem.setVisible(false);
        return true;
    }
}
