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
import android.widget.TextView;
import android.widget.Toast;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.LocalPhotoAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAU on 27.02.2016.
 */
public class LocalAlbumFragment extends Fragment implements VkAlbumView {
    public static final String TAG = LocalAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    public static final String ACTION_MODE_PHOTO_FRAGMENT_ACTIVE = "action_mode_photo_fragment_active";
    private PhotoAlbum photoAlbum;
    private TextView tvCountOfPhotos;
    private LocalAlbumPresenter localAlbumPresenter;
    private List<Photo> photoList = new ArrayList<>();
    private LocalPhotoAdapter adapter;
    private FloatingActionButton fab;
    private MultiSelector multiSelector;

    public static LocalAlbumFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        LocalAlbumFragment fragment = new LocalAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        localAlbumPresenter = new LocalAlbumPresenterImpl(this);
        multiSelector = new MultiSelector();

        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) Logger.d("photoalbum " + photoAlbum.title);
        else Logger.d("wtf where is album?");

        if (photoList.isEmpty())
            photoList = localAlbumPresenter.getPhotosByAlbum(photoAlbum, getContext());
        adapter = new LocalPhotoAdapter(photoList, multiSelector, localAlbumPresenter);
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vk_album, container, false);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE)) {
                localAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) getActivity());
            }
        }

        GridView gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), String.valueOf(photoList.get(position).filePath), Toast.LENGTH_SHORT).show();
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
//                vKAlbumPresenter.addPhotos();
            }
        });
    }

    //lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        Logger.d("LocalAlbumFragment onStart");
        localAlbumPresenter.onStart();
//        if (photoList.isEmpty())
//            photoList = localAlbumPresenter.getPhotosByAlbum(photoAlbum, getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("LocalAlbumFragment onResume");
        setOnClickListenerFab(getView());
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("LocalAlbumFragment onStop");
        localAlbumPresenter.onStop();
    }


    //VkAlbumView implementations
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
    public void deleteSelectedPhoto(MultiSelector multiSelector) {

    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    @Override
    public void confirmDelete(MultiSelector multiSelector) {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE, multiSelector.isSelectable());
    }
}
