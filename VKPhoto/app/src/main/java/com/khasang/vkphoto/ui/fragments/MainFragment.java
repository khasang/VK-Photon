package com.khasang.vkphoto.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.ui.activities.Navigator;
import com.khasang.vkphoto.ui.presenter.MainPresenter;
import com.khasang.vkphoto.ui.presenter.MainPresenterImpl;
import com.khasang.vkphoto.ui.view.MainView;
import com.khasang.vkphoto.util.ToastUtils;

import java.util.List;

public class MainFragment extends Fragment implements MainView {
    public static final String TAG = MainFragment.class.getSimpleName();
    private MainPresenter mainPresenter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mainPresenter = new MainPresenterImpl(this, ((SyncServiceProvider) getActivity()), new Navigator(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.findViewById(R.id.start_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.getAllAlbums();
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void displayVkAlbums(List<PhotoAlbum> photoAlbumList) {
        ToastUtils.showShortMessage("Got albums", getContext());
    }

    @Override
    public void displayGalleryAlbums() {

    }

    @Override
    public void navigateToHome() {

    }

    @Override
    public void showConnectionError() {
        ToastUtils.showError("Error", getContext());
    }

    @Override
    public void showSyncServiceError() {
        ToastUtils.showError("SyncService connecting", getContext());
    }
}

