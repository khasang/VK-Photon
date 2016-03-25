package com.khasang.vkphoto.presentation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.PhotoAlbumAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.presentation.activities.MainActivity;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.AlbumView;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocalAlbumFragment extends Fragment implements AlbumView {
    public static final String TAG = LocalAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    public static final String ACTION_MODE_PHOTO_FRAGMENT_ACTIVE = "action_mode_photo_fragment_active";
    private static final int CAMERA_REQUEST = 1888;
    private PhotoAlbum photoAlbum;
    private TextView tvCountOfPhotos;
    private LocalAlbumPresenter localAlbumPresenter;
    private List<Photo> photoList = new ArrayList<>();
    private PhotoAlbumAdapter adapter;
    private FloatingActionButton fab;
    private MultiSelector multiSelector;
    private int albumId;

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
        setHasOptionsMenu(true);
        localAlbumPresenter = new LocalAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        multiSelector = new MultiSelector();

        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) Logger.d("photoalbum " + photoAlbum.title);
        else Logger.d("wtf where is album?");
        albumId = photoAlbum.id;
        adapter = new PhotoAlbumAdapter(multiSelector, photoList, localAlbumPresenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        tvCountOfPhotos = (TextView) view.findViewById(R.id.tv_photos);
        restoreState(savedInstanceState);
        initFab();
        fab.setImageResource(R.drawable.ic_photo_camera_white_24dp);
        initRecyclerView(view);
        initActionBarHome();
        return view;
    }

    private void initActionBarHome() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE)) {
                localAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) getActivity());
            }
        }
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), MainActivity.PHOTOS_COLUMNS, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        tvCountOfPhotos.setText(getString(R.string.count_of_photos, photoList.size()));
    }

    private void initFab() {
        fab = ((FabProvider) getActivity()).getFloatingActionButton();
        if (!fab.isShown()) {
            fab.show();
        }
    }

    //на самом деле это не метод для удаления фото, а только для отображения этих изменений в адаптере
    //физическое удаление происходит в интерэкторе
    @Override
    public void removePhotosFromView() {
        Logger.d("user wants to removePhotosFromView");
        List<Integer> selectedPositions = multiSelector.getSelectedPositions();
        Collections.sort(selectedPositions, Collections.reverseOrder());
        for (Integer position : selectedPositions) {
            photoList.remove((int) position);
            adapter.notifyItemRemoved(position);
        }
    }

    private void setOnClickListenerFab(View view) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            storeImage(photo);
            localAlbumPresenter.getPhotosByAlbumId(albumId);
        }
    }

    private void storeImage(Bitmap image) {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(photoAlbum.filePath + File.separator + mImageName);
        File pictureFile = mediaFile;
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(pictureFile);
            mediaScanIntent.setData(contentUri);
            getContext().sendBroadcast(mediaScanIntent);
        } catch (FileNotFoundException e) {
            Logger.d( "File not found: " + e.getMessage());
        } catch (IOException e) {
            Logger.d("Error accessing file: " + e.getMessage());
        }
    }
    //lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        Logger.d("LocalAlbumFragment onStart");
        localAlbumPresenter.onStart();
        if (photoList.isEmpty()) {
            localAlbumPresenter.getPhotosByAlbumId(albumId);
        }
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
        fab.setImageResource(R.drawable.ic_add_white_24dp);
        Logger.d("LocalAlbumFragment onStop");
        localAlbumPresenter.onStop();
    }


    //AlbumView implementations
    @Override
    public void displayVkPhotos(List<Photo> photos) {
//        photoList = photos;
        adapter.setPhotoList(photos);
        tvCountOfPhotos.setText(getString(R.string.count_of_photos, photos.size()));
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }




    public void showError(int errorCode) {
        String error = ErrorUtils.getErrorMessage(errorCode, getContext());
        if (error != null) {
            ToastUtils.showError(error, getContext());
        }
    }
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    Navigator.navigateBack(getActivity());
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void confirmDelete ( final MultiSelector multiSelector){
            new MaterialDialog.Builder(getContext())
                    .content(multiSelector.getSelectedPositions().size() > 1 ?
                            R.string.sync_delete_photos_question : R.string.sync_delete_photo_question)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            localAlbumPresenter.deleteSelectedPhotos(multiSelector);
                        }
                    })
                    .show();
        }

        @Override
        public void onSaveInstanceState (Bundle outState){
            super.onSaveInstanceState(outState);
            outState.putBoolean(ACTION_MODE_PHOTO_FRAGMENT_ACTIVE, multiSelector.isSelectable());
        }
    }
