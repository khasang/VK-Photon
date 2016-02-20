package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoAlbumCursorAdapter extends CursorRecyclerViewAdapter<PhotoAlbumViewHolder> {
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    final private Navigator navigator;

    public PhotoAlbumCursorAdapter(Context context, Cursor cursor, MultiSelector multiSelector, Navigator navigator) {
        super(context, cursor);
        executor = Executors.newCachedThreadPool();
        this.multiSelector = multiSelector;
        this.navigator = navigator;
    }

    @Override
    public void onBindViewHolder(PhotoAlbumViewHolder photoAlbumViewHolder, Cursor cursor) {
        photoAlbumViewHolder.bindPhotoAlbum(new PhotoAlbum(cursor));
    }

    @Override
    public PhotoAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new PhotoAlbumViewHolder(view, executor, multiSelector, navigator);
    }

}
