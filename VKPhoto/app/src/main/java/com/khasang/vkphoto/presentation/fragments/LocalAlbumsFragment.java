package com.khasang.vkphoto.presentation.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.LocalAlbumsCursorLoader;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumCursorAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.LocalAlbumsPresenter;
import com.khasang.vkphoto.presentation.presenter.albums.LocalAlbumsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

public class LocalAlbumsFragment extends Fragment implements VkAlbumsView, LoaderManager.LoaderCallbacks<Cursor> {
    private PhotoAlbumCursorAdapter adapter;
    private MultiSelector multiSelector;
    private LocalAlbumsPresenter localAlbumsPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        multiSelector = new MultiSelector();
        localAlbumsPresenter = new LocalAlbumsPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vk_albums, container, false);
        initRecyclerView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().getLoader(1).forceLoad();
    }

    private void setOnClickListenerFab() {
        ((FabProvider) getActivity()).getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("localAlbumsFragment add album");
                new MaterialDialog.Builder(getContext())
                        .title(R.string.create_album)
                        .customView(R.layout.fragment_vk_add_album, true)
                        .positiveText(R.string.create)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View dialogView = dialog.getView();
//                                localAlbumsPresenter.addAlbum(
//                                        ((EditText) dialogView.findViewById(R.id.et_album_title)).getText().toString(),
//                                        ((EditText) dialogView.findViewById(R.id.et_album_description)).getText().toString());
                            }
                        })
                        .show();
            }
        });
    }

    private void initRecyclerView(View view) {
        RecyclerView albumsRecyclerView = (RecyclerView) view.findViewById(R.id.albums_recycler_view);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter(null);
        albumsRecyclerView.setAdapter(adapter);
    }

    private boolean initAdapter(Cursor cursor) {
        if (adapter == null) {
            adapter = new PhotoAlbumCursorAdapter(getContext(), cursor, multiSelector, localAlbumsPresenter);
            return true;
        }
        return false;
    }


    //lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        Logger.d("localAlbumsFragment onStart()");
        localAlbumsPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("localAlbumsFragment onResume()");
        getActivity().getSupportLoaderManager().getLoader(1).forceLoad();
        setOnClickListenerFab();
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("localAlbumsFragment onStop()");
        localAlbumsPresenter.onStop();
    }


    //VkAlbumsView implementations
    @Override
    public void displayVkSaveAlbum(PhotoAlbum photoAlbum) {
        //TODO: implement metod
        Logger.d("displayVkSaveAlbum");
    }

    @Override
    public void displayVkAlbums() {
        //TODO: implement metod
//        for (VKApiPhotoAlbum photoAlbum : photoAlbumList) {
//            Logger.d("id " + photoAlbum.id + "\ntitle " + photoAlbum.title + "\ndescription" + photoAlbum.description + "\nPhoto count " + photoAlbum.size + "\nThumb id " + photoAlbum.thumb_id);
//        }
//        getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Cursor getAdapterCursor() {
        return adapter.getCursor();
    }


    //VkView implementations
    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public void confirmDelete(final MultiSelector multiSelector) {
        StringBuilder content = new StringBuilder();
        content.append(getResources().getQuantityString(R.plurals.sync_delete_album_question_content_1, multiSelector.getSelectedPositions().size()));
        content.append(getResources().getQuantityString(R.plurals.sync_delete_album_question_content_2, multiSelector.getSelectedPositions().size()));
        new MaterialDialog.Builder(getContext())
                .content(content)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        localAlbumsPresenter.deleteSelectedAlbums(multiSelector);
                    }
                })
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new LocalAlbumsCursorLoader(getContext(), new LocalAlbumSource(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!initAdapter(data)) {
            adapter.changeCursor(data);
        }
        int itemCount = adapter.getItemCount();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}
