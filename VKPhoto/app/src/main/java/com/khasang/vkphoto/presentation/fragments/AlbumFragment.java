package com.khasang.vkphoto.presentation.fragments;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumAdapter;
import com.khasang.vkphoto.domain.adapters.SelectAlbumItemAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.listeners.RecyclerViewOnScrollListener;
import com.khasang.vkphoto.presentation.activities.MainActivity;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumFragment extends Fragment implements AlbumView {
    public static final String TAG = AlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    public static final String ACTION_MODE_PHOTO_FRAGMENT_ACTIVE = "action_mode_photo_fragment_active";
    private PhotoAlbum photoAlbum;
    private TextView tvCountOfPhotos;
    private VKAlbumPresenter vkAlbumPresenter;
    private List<Photo> photoList = new ArrayList<>();
    private int albumId;
    private PhotoAlbumAdapter adapter;
    private FloatingActionButton fab;
    private MaterialDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean refreshing;
    private MultiSelector multiSelector;

    public static AlbumFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        multiSelector = new MultiSelector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        tvCountOfPhotos = (TextView) view.findViewById(R.id.tv_photos);
        tvCountOfPhotos.setTypeface(Typeface.createFromAsset(
                getActivity().getAssets(), "fonts/plain.ttf"));
        restoreState(savedInstanceState);
        initFab();
        albumId = photoAlbum.id;
        initReyclerView(view);
        initActionBarHome();
        initSwipeRefreshLayout(view);
        return view;
    }

    private void initActionBarHome() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initReyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), MainActivity.PHOTOS_COLUMNS, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        tvCountOfPhotos.setText(getResources().getString(R.string.count_of_photos, photoList.size()));
        if (PhotoAlbum.checkSelectable(photoAlbum.id)) {
            recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(fab));
        }
    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_album_layout);
        Resources resources = getResources();
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorAccentLight),
                resources.getColor(R.color.colorAccent),
                resources.getColor(R.color.colorAccentDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vkAlbumPresenter.getPhotosByAlbumId(albumId);
            }
        });
    }

    private void restoreState(Bundle savedInstanceState) {
        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (savedInstanceState != null) {
            if (refreshing) {
                displayRefresh(true);
            }
            if (savedInstanceState.getBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE)) {
                vkAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) getActivity());
            }
        } else {
            vkAlbumPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()), photoAlbum);
            adapter = new PhotoAlbumAdapter(multiSelector, photoList, vkAlbumPresenter, photoAlbum);
        }
        if (photoAlbum != null) {
            Logger.d("photoalbum " + photoAlbum.title);
        } else {
            Logger.d("wtf where is album?");
        }
    }

    private void initFab() {
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
        if (PhotoAlbum.checkSelectable(photoAlbum.id)) {
            Logger.d("AlbumFragment. showing fab");
            fab.show();
        } else {
            Logger.d("AlbumFragment. hiding fab");
            fab.hide();
        }
    }

    private void setOnClickListenerFab() {
        if (PhotoAlbum.checkSelectable(photoAlbum.id)) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vkAlbumPresenter.getAllLocalAlbums();
                    progressDialog = new MaterialDialog.Builder(getContext())
                            .title(R.string.load_list_local_albums)
                            .content(R.string.please_wait)
                            .progress(true, 0)
                            .show();
                }
            });
        }
    }

    @Override
    public void displayAllLocalAlbums(final List<PhotoAlbum> albumsList) {
        progressDialog.dismiss();
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_album)
                .adapter(new SelectAlbumItemAdapter(getContext(), albumsList),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                dialog.dismiss();
                                vkAlbumPresenter.goToPhotoAlbum(getContext(), albumsList.get(which), photoAlbum);
                            }
                        })
                .show();
    }

    @Override
    public void displayRefresh(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Logger.d("Refreshing " + refreshing);
                AlbumFragment.this.refreshing = refreshing;
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Navigator.navigateBack(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("VkAlbumFragment onStart()");
        vkAlbumPresenter.onStart();
        if (photoList.isEmpty()) {
            vkAlbumPresenter.getPhotosByAlbumId(albumId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("AlbumFragment onResume()");
        setOnClickListenerFab();
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("VkAlbumFragment onStop()");
        vkAlbumPresenter.onStop();
    }

    @Override
    public void displayPhotos(List<Photo> photos) {
        displayRefresh(false);
        adapter.setPhotoList(photos);
        tvCountOfPhotos.setText(getResources().getString(R.string.count_of_photos, photos.size()));
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }

    @Override
    public void removePhotosFromView() {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (int position : selectedPositions) {
            photoList.remove(position);
        }
        adapter.notifyDataSetChanged();
        tvCountOfPhotos.setText(getResources().getString(R.string.count_of_photos, photoList.size()));
    }


    @Override
    public void showError(int errorCode) {
        Logger.d(TAG + " error " + errorCode);
        switch (errorCode) {
            case ErrorUtils.NO_INTERNET_CONNECTION_ERROR:
                displayRefresh(false);
                ToastUtils.showError(ErrorUtils.getErrorMessage(errorCode, getContext()), getContext());
                break;
        }
    }

    @Override
    public void confirmDelete(final MultiSelector multiSelector) {
        new MaterialDialog.Builder(getContext())
                .content(multiSelector.getSelectedPositions().size() > 1 ?
                        R.string.sync_delete_photos_question : R.string.sync_delete_photo_question)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        vkAlbumPresenter.deleteSelectedPhotos(multiSelector);
                    }
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE, multiSelector.isSelectable());
    }
}
