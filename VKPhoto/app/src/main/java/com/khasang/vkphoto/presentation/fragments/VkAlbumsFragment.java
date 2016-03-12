package com.khasang.vkphoto.presentation.fragments;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.AlbumsCursorLoader;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumCursorAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.VKAlbumsPresenter;
import com.khasang.vkphoto.presentation.presenter.albums.VKAlbumsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumsView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;
import com.vk.sdk.api.model.VKPrivacy;

import java.util.ArrayList;
import java.util.List;

public class VkAlbumsFragment extends Fragment implements VkAlbumsView, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();
    public static final String ACTION_MODE_ACTIVE = "action_mode_active";
    private VKAlbumsPresenter vKAlbumsPresenter;
    private PhotoAlbumCursorAdapter adapter;
    private MultiSelector multiSelector;
    private TextView tvCountOfAlbums;

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
        tvCountOfAlbums = (TextView) view.findViewById(R.id.tv_count_of_albums);
        initRecyclerView(view);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_ACTIVE)) {
                vKAlbumsPresenter.selectAlbum(multiSelector, (AppCompatActivity) getActivity());
            }
        }
        return view;
    }

    private void setOnClickListenerFab() {
        ((FabProvider) getActivity()).getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("VkAlbumsFragment add album");
//                vKAlbumsPresenter.addAlbum();
                new MaterialDialog.Builder(getContext())
                        .title(R.string.create_album)
                        .customView(R.layout.fragment_vk_add_album, true)
                        .positiveText(R.string.create)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View dialogView = dialog.getView();
                                vKAlbumsPresenter.addAlbum(((EditText) dialogView.findViewById(R.id.et_album_title)).getText().toString(),
                                        ((EditText) dialogView.findViewById(R.id.et_album_description)).getText().toString(),
                                        VKPrivacy.PRIVACY_ALL,
                                        VKPrivacy.PRIVACY_ALL);
                            }
                        })
                        .show();
            }
        });
    }

    private void initRecyclerView(View view) {
        RecyclerView albumsRecyclerView = (RecyclerView) view.findViewById(R.id.albums_recycler_view);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            albumsRecyclerView.setHasFixedSize(true);
            albumsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        }
        initAdapter(null);
        albumsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("VkAlbumsFragment onStart()");
        vKAlbumsPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnClickListenerFab();
        Logger.d("VkAlbumsFragment onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("VkAlbumsFragment onStop()");
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
    public Cursor getAdapterCursor() {
        return adapter.getCursor();
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public void confirmDelete(final MultiSelector multiSelector) {
        List<String> names = getNamesSelectedAlbums(multiSelector);
        StringBuilder content = new StringBuilder();
        content.append(getResources().getQuantityString(R.plurals.sync_delete_album_question_content_1, names.size()));
        content.append(" ");
        for (int i = 0; i < names.size(); i++) {
            content.append(names.get(i));
            if (i != names.size() - 1) {
                content.append(", ");
            }
        }
        content.append(" ");
        content.append(getResources().getQuantityString(R.plurals.sync_delete_album_question_content_2, names.size()));
        new MaterialDialog.Builder(getContext())
                .title(getResources().getQuantityString(R.plurals.sync_delete_albums_question_title, names.size()))
                .content(content)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        vKAlbumsPresenter.deleteSelectedAlbums(multiSelector);
                    }
                })
                .show();
    }

    private List<String> getNamesSelectedAlbums(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        List<String> names = new ArrayList<>();
        Cursor cursor = getAdapterCursor();
        if (cursor != null) {
            for (int i = 0, selectedPositionsSize = selectedPositions.size(); i < selectedPositionsSize; i++) {
                Integer position = selectedPositions.get(i);
                cursor.moveToPosition(position);
                names.add(new PhotoAlbum(cursor).toString());
            }
        }
        return names;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AlbumsCursorLoader(getContext(), new LocalAlbumSource(getContext()));
    }

    private boolean initAdapter(Cursor cursor) {
        if (adapter == null) {
            adapter = new PhotoAlbumCursorAdapter(getContext(), cursor, multiSelector, vKAlbumsPresenter);
            return true;
        }
        return false;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!initAdapter(data)) {
            adapter.changeCursor(data);
        }
        int itemCount = adapter.getItemCount();
        tvCountOfAlbums.setText(getResources().getQuantityString(R.plurals.count_of_albums, itemCount, itemCount));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_ACTIVE, multiSelector.isSelectable());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

}

