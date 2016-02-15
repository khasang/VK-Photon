package com.khasang.vkphoto.ui.fragments;

import android.database.Cursor;
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

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumsAdapter;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.data.AlbumsCursorLoader;
import com.khasang.vkphoto.model.data.local.LocalAlbumSource;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.ui.presenter.VKAlbumsPresenterImpl;
import com.khasang.vkphoto.ui.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;
import java.util.List;

public class VkAlbumsFragment extends Fragment implements VkAlbumsView, LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();
    private VKAlbumsPresenter vKAlbumsPresenter;
    private RecyclerView albumsRecyclerView;
    private List<PhotoAlbum> albumsToSync;

    public VkAlbumsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        vKAlbumsPresenter = new VKAlbumsPresenterImpl(this, ((SyncServiceProvider) getActivity()), new Navigator(getActivity()), getContext());
        albumsToSync = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView tv_count_of_albums = (TextView) view.findViewById(R.id.tv_count_of_albums);
        albumsRecyclerView = (RecyclerView) view.findViewById(R.id.albums_recycler_view);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.start_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_count_of_albums.getVisibility() == View.INVISIBLE){
                    tv_count_of_albums.setVisibility(View.VISIBLE);
                }
                vKAlbumsPresenter.getAllAlbums();
            }
        });
        return view;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void displayVkAlbums(List<PhotoAlbum> photoAlbumList) {
        for (VKApiPhotoAlbum photoAlbum : photoAlbumList) {
            Logger.d("id " + photoAlbum.id + "\ntitle " + photoAlbum.title + "\ndescription" + photoAlbum.description + "\nPhoto count " + photoAlbum.size + "\nThumb id " + photoAlbum.thumb_id);
        }
        PhotoAlbumsAdapter adapter = new PhotoAlbumsAdapter(photoAlbumList, albumsToSync);
        albumsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AlbumsCursorLoader(getContext(),new LocalAlbumSource(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

