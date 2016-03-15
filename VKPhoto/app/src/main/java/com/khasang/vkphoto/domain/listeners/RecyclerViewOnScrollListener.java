package com.khasang.vkphoto.domain.listeners;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private FloatingActionButton fab;

    public RecyclerViewOnScrollListener(FloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0 && fab.isShown())
            fab.hide();
        else if (dy < 0 && !fab.isShown())
            fab.show();
    }
}
