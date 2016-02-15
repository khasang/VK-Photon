package com.khasang.vkphoto.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;

public class GalleryFragment extends Fragment {
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();

    public GalleryFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View galleryAlbumsView = inflater.inflate(R.layout.fragment_gallery_albums, null);
        return galleryAlbumsView;

    }
}
