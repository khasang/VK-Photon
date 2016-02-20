package com.khasang.vkphoto.presentation.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.fragments.VkAlbumsFragment;

public class Navigator {

    private static FragmentManager getFragmentManager(Context context) {
        return ((FragmentActivity) context).getSupportFragmentManager();
    }

    private static void startActivity(Intent intent, Context context) {
        context.startActivity(intent);
    }

    private static boolean isFragmentAvailable(Fragment fragment) {
        return fragment != null && fragment.isAdded();
    }

    public static void navigateToVKAlbumsFragment(Context context) {
        FragmentManager fragmentManager = getFragmentManager(context);
        Fragment fragment = fragmentManager.findFragmentByTag(VkAlbumsFragment.TAG);
        if (!isFragmentAvailable(fragment)) {
            fragment = new VkAlbumsFragment();
        }
        navigateToFragment(context, fragment, VkAlbumsFragment.TAG);
    }

    private static void navigateToFragment(Context context, Fragment fragment, String tag) {
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }

    private static void navigateToFragmentWithBackStack(Context context, Fragment fragment, String tag) {
        getFragmentManager(context).beginTransaction().add(R.id.fragment_container, fragment, tag).addToBackStack(tag).commit();
    }

    public static void navigateBack(Context context) {
        FragmentManager fragmentManager = getFragmentManager(context);
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            ((Activity) context).finish();
        }
    }
}
