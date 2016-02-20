package com.khasang.vkphoto.presentation.fragments;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.AlbumsCursorLoader;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumCursorAdapter;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.NetWorkUtils;
import com.khasang.vkphoto.util.ToastUtils;

import java.util.List;

public class VkAlbumsFragment extends Fragment implements VkAlbumsView, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();
    private VKAlbumsPresenter vKAlbumsPresenter;
    private RecyclerView albumsRecyclerView;
    private PhotoAlbumCursorAdapter adapter;
    private MultiSelector multiSelector;

    public VkAlbumsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        multiSelector = new MultiSelector();
        vKAlbumsPresenter = new VKAlbumsPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vk_albums, container, false);
        final TextView tv_count_of_albums = (TextView) view.findViewById(R.id.tv_count_of_albums);
        view.findViewById(R.id.start_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check internet connection
                int networkType = NetWorkUtils.getNetworkType(getContext());
                if (networkType == ConnectivityManager.TYPE_WIFI) {
                    Logger.d("Connection WiFi");
                } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
                    Logger.d("Connection mobile");
                } else {
                    Logger.d("no internet connection");
                }
                List<Integer> selectedPositions = multiSelector.getSelectedPositions();
                Cursor cursor = adapter.getCursor();
                for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                    Integer position = selectedPositions.get(i);
                    cursor.moveToPosition(position);
                    PhotoAlbum photoAlbum = new PhotoAlbum(cursor);
                    Logger.d(photoAlbum.title + " " + photoAlbum.id);
                }
            }
        });
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        albumsRecyclerView = (RecyclerView) view.findViewById(R.id.albums_recycler_view);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        albumsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        vKAlbumsPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        vKAlbumsPresenter.onStop();
    }

    @Override
    public void displayVkSaveAlbum(PhotoAlbum photoAlbum) {
        Logger.d("displayVkSaveAlbum");
    }

    @Override
    public void displayVkAlbums() {
//        for (VKApiPhotoAlbum photoAlbum : photoAlbumList) {
//            Logger.d("id " + photoAlbum.id + "\ntitle " + photoAlbum.title + "\ndescription" + photoAlbum.description + "\nPhoto count " + photoAlbum.size + "\nThumb id " + photoAlbum.thumb_id);
//        }
        getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AlbumsCursorLoader(getContext(), new LocalAlbumSource(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter == null) {
            adapter = new PhotoAlbumCursorAdapter(getContext(), data, multiSelector);
            albumsRecyclerView.setAdapter(adapter);
        } else {
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vKAlbumsPresenter.getAllAlbums();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

}

