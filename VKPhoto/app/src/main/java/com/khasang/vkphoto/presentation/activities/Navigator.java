package com.khasang.vkphoto.presentation.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumFragment;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumsFragment;
import com.khasang.vkphoto.presentation.fragments.VKAlbumFragment;
import com.khasang.vkphoto.presentation.fragments.VKAlbumsFragment;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;

import java.util.List;

public class Navigator {

    private static int mode = Constants.START;
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
        navigateToFragmentWithBackStack(context, VKAlbumFragment.newInstance(photoAlbum), photoAlbum.title);
    }

    public static void navigateToLocalAlbumFragment(Context context, PhotoAlbum photoAlbum) {
        navigateToFragmentWithBackStack(context, LocalAlbumFragment.newInstance(photoAlbum), photoAlbum.title);
    }

    private static void navigateToFragment(Context context, Fragment fragment, String tag) {
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, tag).commit();

    }

    private static void navigateToFragmentWithBackStack(Context context, Fragment fragment, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        changeActionBarTitle(activity, title);
        changeViewPagerVisibility(activity, false);
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, title).addToBackStack(title).commit();
    }

    private static void changeActionBarTitle(AppCompatActivity activity, String title) {
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
    }

    public static void navigateBack(Context context) {
        FragmentManager fragmentManager = getFragmentManager(context);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (int i = fragments.size() - 2; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (!(fragment instanceof VKAlbumsFragment) && !(fragment instanceof LocalAlbumsFragment)) {
                    return;
                }
            }
            changeViewPagerVisibility((Activity) context, true);
            Fragment fragment = fragmentManager.getFragments()
                    .get(tabPosition);
            changeActionBarTitle((AppCompatActivity) context, context.getString(R.string.app_name));
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

    public static void initToolbar(Toolbar toolbar) {

    }
}
