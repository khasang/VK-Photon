package com.khasang.vkphoto.presentation.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.ButtonItemAdapter;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.Logger;

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
        vkAlbumPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        multiSelector = new MultiSelector();
        adapter = new PhotoAlbumAdapter(multiSelector, photoList, vkAlbumPresenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        tvCountOfPhotos = (TextView) view.findViewById(R.id.tv_photos);
        restoreState(savedInstanceState);
        initFab();
        albumId = photoAlbum.id;
        initReyclerView(view);
        initActionBarHome();
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE)) {
                vkAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) getActivity());
            }
        }
        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) {
            Logger.d("photoalbum " + photoAlbum.title);
        } else {
            Logger.d("wtf where is album?");
        }
    }

    private void initFab() {
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
        if (!fab.isShown()) {
            fab.show();
        }
    }

    private void setOnClickListenerFab(View view) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vkAlbumPresenter.getLocalAlbumsCursor();
            }
        });
    }

    BitmapFactory.Options createBitmap(String stPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(stPath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        return options;
    }

    @Override
    public void displayAllLocalAlbums(final List<PhotoAlbum> albumsList){
//        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(getContext());
//        int defWidth = 200;
//        int defHeight = 200;
////        Drawable d = BitmapDrawable(getResources(), BitmapFactory.decodeFile(albumsList.get(0).thumbFilePath));
//        for (int ind = 0; ind < 5; ind++) {
////        for (int ind = 0; ind < albumsList.size(); ind++) {
//            adapter.add(new MaterialSimpleListItem.Builder(getContext())
//                    .content(albumsList.get(ind).title)
//                    .icon(Drawable.createFromPath(albumsList.get(ind).thumbFilePath))
////                    .icon(d)
//                    .build());
//        }
//        new MaterialDialog.Builder(getContext())
//                .title(R.string.select_album)
//                .adapter(adapter, new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                        MaterialSimpleListItem item = adapter.getItem(which);
//                        dialog.dismiss();
//                        vkAlbumPresenter.goToPhotoAlbum(getContext(), albumsList.get(which));
//                    }
//                })
//                .show();
        List<PhotoAlbum> list = new ArrayList<PhotoAlbum>();
        list.add(albumsList.get(11));
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_album)
                .adapter(new ButtonItemAdapter(getContext(), list),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                Toast.makeText(getContext(), "Clicked item " + which, Toast.LENGTH_SHORT).show();
                            }
                        })
                .show();
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
        setOnClickListenerFab(getView());
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("VkAlbumFragment onStop()");
        vkAlbumPresenter.onStop();
    }

    @Override
    public void displayVkPhotos(List<Photo> photos) {
        photoList = photos;
        adapter.setPhotoList(photos);
        tvCountOfPhotos.setText(getResources().getString(R.string.count_of_photos, photos.size()));
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }

    @Override
    public void removePhotosFromView(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (Integer position : selectedPositions)
            photoList.remove((int) position);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void showError(int errorCode) {
        Logger.d(TAG + " error " + errorCode);
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
