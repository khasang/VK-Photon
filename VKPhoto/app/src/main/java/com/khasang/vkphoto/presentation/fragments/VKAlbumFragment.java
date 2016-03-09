package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.VKPhotoAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VKAlbumFragment extends Fragment implements VkAlbumView {
    public static final String TAG = VKAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    public static final String ACTION_MODE_PHOTO_FRAGMENT_ACTIVE = "action_mode_photo_fragment_active";
    private PhotoAlbum photoAlbum;
    private TextView tvCountOfPhotos;
    private VKAlbumPresenter vkAlbumPresenter;
    private List<Photo> photoList = new ArrayList<>();
    private int albumId;
    private VKPhotoAdapter adapter;
    private FloatingActionButton fab;
    private MultiSelector multiSelector;

    public static VKAlbumFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        VKAlbumFragment fragment = new VKAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        vkAlbumPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        multiSelector = new MultiSelector();
        adapter = new VKPhotoAdapter(photoList, multiSelector, vkAlbumPresenter);
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vk_album, container, false);
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
        albumId = photoAlbum.id;
        GridView gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                vkAlbumPresenter.deleteSelectedVkPhotos(photoList.get(position).getId());
//                photoList.remove(position);
//                adapter.notifyDataSetChanged();
//                EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
            }
        });
        adapter.setLoaded(false);
        gridview.setAdapter(adapter);
        tvCountOfPhotos = (TextView) view.findViewById(R.id.tv_photos);
        return view;
    }

    private void setOnClickListenerFab(View view) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OpenFileDialog fileDialog = new OpenFileDialog(getContext(), getActivity());
                fileDialog.show();
                fileDialog.setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                    @Override
                    public void OnSelectedFile(ArrayList<String> listSelectedFiles) {
//                        vKPhotosPresenter.addPhotos(listSelectedFiles, photoAlbum);
                    }
                });
                ToastUtils.showShortMessage("Here will be action Add Photos", getActivity());
//                vkAlbumPresenter.addPhotos();
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
        Logger.d("VkAlbumFragment onStart");
        vkAlbumPresenter.onStart();
        if (photoList.isEmpty()) {
            vkAlbumPresenter.getPhotosByAlbumId(albumId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("VKAlbumFragment onResume");
        setOnClickListenerFab(getView());
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("VkAlbumFragment onStop");
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
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
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
                        vkAlbumPresenter.deleteSelectedVkPhotos(multiSelector);
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
