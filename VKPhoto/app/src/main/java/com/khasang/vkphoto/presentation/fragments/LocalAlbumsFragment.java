package com.khasang.vkphoto.presentation.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.LocalAlbumsCursorLoader;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumsCursorAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.RecyclerViewOnScrollListener;
import com.khasang.vkphoto.presentation.custom_classes.GridSpacingItemDecoration;
import com.khasang.vkphoto.presentation.model.MyOnQuerrySearchListener;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.LocalAlbumsPresenter;
import com.khasang.vkphoto.presentation.presenter.albums.LocalAlbumsPresenterImpl;
import com.khasang.vkphoto.presentation.view.AlbumsView;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.khasang.vkphoto.util.Constants.ALBUMS_SPAN_COUNT;

public class LocalAlbumsFragment extends Fragment implements AlbumsView, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ACTION_MODE_ACTIVE = "action_mode_active";
    private static final String TAG = LocalAlbumsFragment.class.getSimpleName();
    private PhotoAlbumsCursorAdapter adapter;
    private MultiSelector multiSelector;
    private LocalAlbumsPresenter localAlbumsPresenter;
    private MyOnQuerrySearchListener myOnQuerrySearchListener = new MyOnQuerrySearchListener();
    private TextView tvCountOfAlbums;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean refreshing;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        localAlbumsPresenter = new LocalAlbumsPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        multiSelector = new MultiSelector();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(this.toString());
        Logger.d("" + getTag());
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
        tvCountOfAlbums = (TextView) view.findViewById(R.id.tv_count_of_albums);
        tvCountOfAlbums.setTypeface(Typeface.createFromAsset(
                getActivity().getAssets(), "fonts/plain.ttf"));
        initRecyclerView(view);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_ACTIVE)) {
                localAlbumsPresenter.selectAlbum(multiSelector, (AppCompatActivity) getActivity());
            }
            if (refreshing) {
                displayRefresh(true);
            }
        }
        initSwipeRefreshLayout(view);
        return view;
    }

    private void setOnClickListenerFab() {
        ((FabProvider) getActivity()).getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("localAlbumsFragment add album");
                new MaterialDialog.Builder(getContext())
                        .title(R.string.create_album)
                        .customView(R.layout.fragment_local_create_edit_album, true)
                        .positiveText(R.string.create)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View dialogView = dialog.getView();
                                localAlbumsPresenter.addAlbum(((EditText) dialogView.findViewById(R.id.et_local_album_title)).getText().toString());
                            }
                        })
                        .show();
            }
        });
    }

    private boolean initAdapter(Cursor cursor) {
        if (adapter == null) {
            adapter = new PhotoAlbumsCursorAdapter(getContext(), cursor, multiSelector, localAlbumsPresenter);
            return true;
        }
        return false;
    }


    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        Resources resources = getResources();
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorAccentLight),
                resources.getColor(R.color.colorAccent),
                resources.getColor(R.color.colorAccentDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAlbums();
            }
        });
    }

    private void initRecyclerView(View view) {
        RecyclerView albumsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            albumsRecyclerView.setHasFixedSize(true);
            albumsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), ALBUMS_SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
            albumsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(ALBUMS_SPAN_COUNT, Constants.RECYCLERVIEW_SPACING, false));
        }
        initAdapter(null);
        albumsRecyclerView.setAdapter(adapter);
        albumsRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(((FabProvider) getActivity()).getFloatingActionButton()));
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
        setOnClickListenerFab();
        getActivity().getSupportLoaderManager().getLoader(1).forceLoad();
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("localAlbumsFragment onStop()");
        localAlbumsPresenter.onStop();
    }


    @Override
    public void confirmSync(MultiSelector multiSelector) {
        //upload to VK
    }

    //AlbumsView implementations
    @Override
    public void displayVkSaveAlbum(PhotoAlbum photoAlbum) {
        //TODO: implement metod
        Logger.d("displayVkSaveAlbum");
    }

    @Override
    public void displayAlbums() {
        getActivity().getSupportLoaderManager().getLoader(1).forceLoad();
    }

    public void displayDeletedAlbums() {
        //
        Logger.d("user wants to removePhotosFromView");
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (Integer position : selectedPositions) {
            adapter.notifyItemRemoved(position);
        }
        displayAlbums();
    }

    @Override
    public void displayRefresh(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Logger.d("startRefreshing");
                LocalAlbumsFragment.this.refreshing = refreshing;
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public Cursor getAdapterCursor() {
        return adapter.getCursor();
    }

    @Override
    public void editAlbum(final PhotoAlbum photoAlbum) {
        View view = View.inflate(getContext(), R.layout.fragment_local_create_edit_album, null);
        ((EditText) view.findViewById(R.id.et_local_album_title)).setText(photoAlbum.title);
        new MaterialDialog.Builder(getContext())
                .title(R.string.edit_album)
                .customView(view, true)
                .positiveText(R.string.st_btn_ok)
                .negativeText(R.string.st_btn_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View dialogView = dialog.getView();
                        String newTitle = ((EditText) dialogView.findViewById(R.id.et_local_album_title)).getText().toString();
                        photoAlbum.title = newTitle;
                        localAlbumsPresenter.editLocalOrSyncAlbum(photoAlbum, newTitle);
                    }
                })
                .show();
    }

    @Override
    public void editPrivacyOfAlbums(List<PhotoAlbum> albumsList, int oldPrivacy) {

    }


    //View implementations

    @Override
    public void showError(int errorCode) {
        Logger.d("LocalAlbumsFragment error " + errorCode);
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
        return new LocalAlbumsCursorLoader(getContext(), new LocalAlbumSource(getContext()));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            do {
                PhotoAlbum photoAlbum = new PhotoAlbum(data);
                Logger.d("LocalAlbumsFragment. onLoadFinished. ID=" + photoAlbum.id + ", name=" + photoAlbum.title + ", size=" + photoAlbum.size);
            } while (data.moveToNext());
            data.close();
        }
        if (!initAdapter(data)) {
            adapter.changeCursor(data);
        }
        int itemCount = adapter.getItemCount();
        tvCountOfAlbums.setText(getResources().getQuantityString(R.plurals.count_of_albums, itemCount, itemCount));
        displayRefresh(false);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_albums, menu);
//        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
//        MenuItem microMenuItem = menu.findItem(R.id.action_micro);
//        SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
//        mSearchView.setOnQueryTextListener(myOnQuerrySearchListener);
//        searchMenuItem
//                .setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
//                        | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//        MyActionExpandListener myActionExpandListener = new MyActionExpandListener(microMenuItem);
//        MenuItemCompat.setOnActionExpandListener(searchMenuItem, myActionExpandListener);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
