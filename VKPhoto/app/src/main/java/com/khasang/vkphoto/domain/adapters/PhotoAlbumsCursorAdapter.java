package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.viewholders.PhotoAlbumViewHolder;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.AlbumsPresenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoAlbumsCursorAdapter extends CursorRecyclerViewAdapter<PhotoAlbumViewHolder> {
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    private AlbumsPresenter albumsPresenter;

    public PhotoAlbumsCursorAdapter(Context context, Cursor cursor, MultiSelector multiSelector, AlbumsPresenter albumsPresenter) {
        super(context, cursor);
        executor = Executors.newCachedThreadPool();
        this.multiSelector = multiSelector;
        this.albumsPresenter = albumsPresenter;
    }

    @Override
    public void onBindViewHolder(PhotoAlbumViewHolder photoAlbumViewHolder, Cursor cursor) {
        if (cursor == null) return;
        photoAlbumViewHolder.bindPhotoAlbum(new PhotoAlbum(cursor));
    }

    @Override
    public PhotoAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new PhotoAlbumViewHolder(view, executor, multiSelector, albumsPresenter);
    }

}
