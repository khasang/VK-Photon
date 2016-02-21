package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.VKPhotoAdapter;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.interfaces.NavigatorProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;
import com.khasang.vkphoto.presentation.presenter.VKPhotosPresenter;
import com.khasang.vkphoto.presentation.presenter.VKPhotosPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkPhotosView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class VKPhotosFragment extends Fragment implements VkPhotosView {
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    private PhotoAlbum photoAlbum;
    private VKPhotosPresenter vKPhotosPresenter;
    private Navigator navigator;
    GridView gridview;
    private List<Photo> photoList;
    int albumId;
    private EventBus eventBus;

    public static VKPhotosFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        VKPhotosFragment fragment = new VKPhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_albums, null);
        navigator = ((NavigatorProvider) getActivity()).getNavigator();
        vKPhotosPresenter = new VKPhotosPresenterImpl(this, ((SyncServiceProvider) getActivity()), navigator, getContext());

        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) {
            Logger.d("photoalbum " + photoAlbum.title);
        } else {
            Logger.d("wtf where is album?");
        }
        albumId = photoAlbum.id;
        vKPhotosPresenter.getPhotosByAlbumId(albumId);
        gridview = (GridView) view.findViewById(R.id.gridView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume VKPhotosFragment");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        vKPhotosPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        vKPhotosPresenter.onStop();
    }

    @Override
    public void displayVkPhotosByAlbumId() {

    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }


    private void updateAdapter() {
        gridview.setAdapter(new VKPhotoAdapter(getContext(), photoList));
        Log.d("333", "sdsd");

    }

    @Subscribe
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        List<Photo> vkPhotoList = getVKPhotosEvent.photosList;
        photoList = vkPhotoList;
        updateAdapter();
    }

}
