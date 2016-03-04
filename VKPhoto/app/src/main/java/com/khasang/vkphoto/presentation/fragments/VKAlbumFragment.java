package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.VKPhotoAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VKAlbumFragment extends Fragment implements VkAlbumView {
    public static final String TAG = VKAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    public static final String ACTION_MODE_PHOTO_FRAGMENT_ACTIVE = "action_mode_photo_fragment_active";
    private PhotoAlbum photoAlbum;
    private VKAlbumPresenter vKAlbumPresenter;
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
        vKAlbumPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        multiSelector = new MultiSelector();
        adapter = new VKPhotoAdapter(photoList, multiSelector, vKAlbumPresenter);
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vk_album, null);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE)) {
                vKAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) getActivity());
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
//                vKAlbumPresenter.deleteSelectedVkPhotos(photoList.get(position).getId());
//                photoList.remove(position);
//                adapter.notifyDataSetChanged();
//                EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
            }
        });
        adapter.setLoaded(false);
        gridview.setAdapter(adapter);
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
//                vKAlbumPresenter.addPhotos();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnClickListenerFab(getView());
        Logger.d("onResume VKAlbumFragment");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("VkAlbumFragment onStart");
        vKAlbumPresenter.onStart();
        if (photoList.isEmpty()) {
            vKAlbumPresenter.getPhotosByAlbumId(albumId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        vKAlbumPresenter.onStop();
    }

    @Override
    public void displayVkPhotos(List<Photo> photos) {
        photoList = photos;
        adapter.setPhotoList(photos);
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }

    @Override
    public void deleteSelectedPhoto(MultiSelector multiSelector) {
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (Integer position : selectedPositions) {
            photoList.remove(position);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE, multiSelector.isSelectable());
    }

}
