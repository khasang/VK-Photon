package com.khasang.vkphoto.presentation.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.LocalAlbumsCursorLoader;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumCursorAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.MyActionExpandListener;
import com.khasang.vkphoto.presentation.model.MyOnQuerrySearchListener;
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
    private MenuItem searchMenuItem;
    private SearchView mSearchView;
    private MyActionExpandListener myActionExpandListener;
    private MyOnQuerrySearchListener myOnQuerrySearchListener = new MyOnQuerrySearchListener();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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
        new MaterialDialog.Builder(getContext())
                .content(R.string.sync_delete_album_question)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        localAlbumsPresenter.deleteAlbums(multiSelector);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAlbumThumb;
        private TextView mAlbumTitle;
        private TextView mPhotosCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAlbumThumb = (ImageView) itemView.findViewById(R.id.album_thumb);
            mAlbumTitle = (TextView) itemView.findViewById(R.id.album_title);
            mPhotosCount = (TextView) itemView.findViewById(R.id.tv_count_of_photos);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_albums, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        MenuItem microMenuItem = menu.findItem(R.id.action_micro);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(myOnQuerrySearchListener);
        searchMenuItem
                .setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                        | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        myActionExpandListener = new MyActionExpandListener(microMenuItem);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, myActionExpandListener);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
