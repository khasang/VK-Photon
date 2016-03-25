package com.khasang.vkphoto.presentation.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.PhotoViewPagerAdapter;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class PhotoViewPagerFragment extends Fragment {

    private static final String PHOTO_LIST = "photoList";
    private static final String POSITION = "position";

    public static final String TAG = PhotoViewPagerFragment.class.getSimpleName();

    private List<Photo> photoList;
    private int position;
    private ViewPager viewPager;
    private PhotoViewPagerAdapter adapter;
    private FragmentActivity myContext;

    public static PhotoViewPagerFragment newInstance(List<Photo> photoList, int position) {
        Logger.d(TAG + " onNewInstance");
        PhotoViewPagerFragment fragment = new PhotoViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PHOTO_LIST, (ArrayList) photoList);
        args.putInt(POSITION,position);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG + " onCreate");
        if (getArguments() != null) {
            photoList = getArguments().getParcelableArrayList(PHOTO_LIST);
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d(TAG + " onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo_view_pager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.photoListPager);
        adapter = new PhotoViewPagerAdapter(myContext.getSupportFragmentManager(),photoList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG + " onStop");
        adapter = null;

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG + " onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d(TAG + " onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG + " onDestroy");
    }

}
