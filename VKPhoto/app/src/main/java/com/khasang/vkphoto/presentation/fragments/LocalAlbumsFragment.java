package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;

public class LocalAlbumsFragment extends Fragment {
    public static final String TAG = VkAlbumsFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photoalbum_item, container, false);
        return view;
    }
}
