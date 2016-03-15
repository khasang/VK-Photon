package com.khasang.vkphoto.presentation.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.fragments.AlbumFragment;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumFragment;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

public class Navigator {
    private static int tabPosition = 0;

    private static FragmentManager getFragmentManager(Context context) {
        return ((FragmentActivity) context).getSupportFragmentManager();
    }

    private static void startActivity(Intent intent, Context context) {
        context.startActivity(intent);
    }

    private static boolean isFragmentAvailable(Fragment fragment) {
        return fragment != null && fragment.isAdded();
    }

    public static void navigateToVKAlbumFragment(Context context, PhotoAlbum photoAlbum) {
        navigateToFragmentWithBackStack(context, AlbumFragment.newInstance(photoAlbum), AlbumFragment.TAG);
    }

    public static void navigateToLocalAlbumFragment(Context context, PhotoAlbum photoAlbum) {
        navigateToFragmentWithBackStack(context, LocalAlbumFragment.newInstance(photoAlbum), LocalAlbumFragment.TAG);
    }

    private static void navigateToFragment(Context context, Fragment fragment, String tag) {
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }

    private static void navigateToFragmentWithBackStack(Context context, Fragment fragment, String tag) {
        changeViewPagerVisibility(((Activity) context), false);
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, tag).addToBackStack(tag).commit();
    }

    public static void navigateBack(Context context) {
        FragmentManager fragmentManager = getFragmentManager(context);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment fragment;
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            fragmentManager.popBackStack();
            if (backStackEntryCount == 1) {
                changeViewPagerVisibility((Activity) context, true);
                fragment = fragmentManager.getFragments()
                        .get(tabPosition);
                ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setDisplayHomeAsUpEnabled(false);
                }
            } else {
                FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
                String str = backEntry.getName();
                fragment = fragmentManager.findFragmentByTag(str);
            }
            fragment.onResume();
        } else {
            ((Activity) context).finish();
        }
    }

    public static void changeViewPagerVisibility(Activity activity, boolean visibility) {
        View tabLayout = activity.findViewById(R.id.tabs);
        View fragmentContainer = activity.findViewById(R.id.fragment_container);
        View viewPager = activity.findViewById(R.id.viewpager);
        if (visibility) {
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        } else {
            if (viewPager.getVisibility() == View.VISIBLE) {
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setTabPosition(int tabPosition) {
        Navigator.tabPosition = tabPosition;
    }
}
