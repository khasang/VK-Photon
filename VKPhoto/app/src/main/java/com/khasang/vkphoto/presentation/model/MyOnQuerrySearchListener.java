package com.khasang.vkphoto.presentation.model;

import android.support.v7.widget.SearchView;

/**
 * Created by Иричи on 10.03.2016.
 */
public class MyOnQuerrySearchListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
