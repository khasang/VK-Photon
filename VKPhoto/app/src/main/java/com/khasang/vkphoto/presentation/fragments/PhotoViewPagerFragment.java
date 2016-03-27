package com.khasang.vkphoto.presentation.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.PhotoViewPagerAdapter;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class PhotoViewPagerFragment extends Fragment {

    private static final String PHOTO_LIST = "photoList";
    private static final String POSITION = "position";
    public static String CRUTCH = "";
    public static final String TAG = PhotoViewPagerFragment.class.getSimpleName();

    private List<Photo> photoList;
    private int position;
    private ViewPager viewPager;
    private PhotoViewPagerAdapter adapter;
    private FragmentActivity fragmentActivity;

    public static PhotoViewPagerFragment newInstance(List<Photo> photoList, int position) {
        Logger.d(TAG + " onNewInstance");
        PhotoViewPagerFragment fragment = new PhotoViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PHOTO_LIST, (ArrayList) photoList);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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
        adapter = new PhotoViewPagerAdapter(fragmentActivity.getSupportFragmentManager(), photoList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Navigator.changePhotoTitle((AppCompatActivity) getContext(), photoList, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity) context;
        CRUTCH = "alive";
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG + " onStop");
        adapter = null;
        CRUTCH = "dead";
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Navigator.navigateBack(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
