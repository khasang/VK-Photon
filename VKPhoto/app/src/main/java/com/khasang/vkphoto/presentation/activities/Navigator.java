package com.khasang.vkphoto.presentation.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.ViewPagerAdapter;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumsFragment;
import com.khasang.vkphoto.presentation.fragments.VKAlbumFragment;
import com.khasang.vkphoto.presentation.fragments.VkAlbumsFragment;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;

import java.util.List;

public class Navigator {
    private Context activityContext;
    private VKAlbumFragment vkAlbumFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FrameLayout fragmentContainer;

    public Navigator(Context activityContext) {
        this.activityContext = activityContext;
    }

    private FragmentManager getFragmentManager() {
        return ((FragmentActivity) activityContext).getSupportFragmentManager();
    }

    private void startActivity(Intent intent) {
        activityContext.startActivity(intent);
    }

    private boolean isFragmentAvailable(Fragment fragment) {
        return fragment != null && fragment.isAdded();
    }

    public void navigateToVkAlbumFragment(PhotoAlbum photoAlbum) {
        if (!isFragmentAvailable(vkAlbumFragment)) {
            vkAlbumFragment = VKAlbumFragment.newInstance(photoAlbum);
        }
        navigateToFragmentWithBackStack(vkAlbumFragment, VkAlbumsFragment.TAG);
    }

//    private void navigateToFragment(Fragment fragment, String tag) {
//        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
//    }

    private void navigateToFragmentWithBackStack(Fragment fragment, String tag) {
        if (viewPager.getVisibility() == View.VISIBLE) {
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).addToBackStack(tag).commit();
    }

    public void navigateBack() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (int i = fragments.size() - 2; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (!(fragment instanceof VkAlbumsFragment) && !(fragment instanceof LocalAlbumsFragment)) {
                    return;
                }
            }
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        } else {
            ((Activity) activityContext).finish();
        }
    }

    public void initViewPager() {
        fragmentContainer = (FrameLayout) ((Activity) activityContext).findViewById(R.id.fragment_container);
        viewPager = (ViewPager) ((Activity) activityContext).findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) ((Activity) activityContext).findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.d("page " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new VkAlbumsFragment(), "VK Albums");
        adapter.addFragment(new LocalAlbumsFragment(), "Gallery Albums");
        viewPager.setAdapter(adapter);
    }
}
