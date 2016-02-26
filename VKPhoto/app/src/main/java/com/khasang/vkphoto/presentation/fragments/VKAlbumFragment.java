package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.VKPhotoAdapter;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.presenter.VKPhotosPresenter;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VKAlbumFragment extends Fragment implements VkAlbumView {
    public static final String TAG = VKAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    private PhotoAlbum photoAlbum;
    private VKPhotosPresenter vKPhotosPresenter;
    GridView gridview;
    private List<Photo> photoList = new ArrayList<>();
    int albumId;
    private VKPhotoAdapter adapter;
    private FloatingActionButton fab;

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
        vKPhotosPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        photoList = new ArrayList<>();
        adapter = new VKPhotoAdapter(getContext(), photoList);
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_albums, null);
        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) {
            Logger.d("photoalbum " + photoAlbum.title);
        } else {
            Logger.d("wtf where is album?");
        }
        albumId = photoAlbum.id;
        gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vKPhotosPresenter.deletePhotoById(photoList.get(position).getId());
                photoList.remove(position);
                adapter.notifyDataSetChanged();
                EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
            }
        });
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
                    public void OnSelectedFile(Vector<String> listSelectedFiles) {
                        vKPhotosPresenter.addPhotos(listSelectedFiles, photoAlbum);
                    }
                });
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
        vKPhotosPresenter.onStart();
        vKPhotosPresenter.getPhotosByAlbumId(albumId);
    }

    @Override
    public void onStop() {
        super.onStop();
        vKPhotosPresenter.onStop();
    }

    @Override
    public void displayVkPhotos(List<Photo> photos) {
        photoList = photos;
        adapter.setPhotoList(photos);
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }



}
