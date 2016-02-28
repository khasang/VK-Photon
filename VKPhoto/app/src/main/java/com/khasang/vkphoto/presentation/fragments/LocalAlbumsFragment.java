package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;

public class LocalAlbumsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //       List <PhotoAlbum> allAlbumsList = localAlbumSource.getAllAlbums();
//        for (PhotoAlbum album: allAlbumsList) {
//            addAlbumViewToLayout(album, localAlbumsGallery);
//        }
        return inflater.inflate(R.layout.fragment_vk_albums, container, false);
    }
}
