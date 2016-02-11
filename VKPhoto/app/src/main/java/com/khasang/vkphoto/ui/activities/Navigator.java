package com.khasang.vkphoto.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.ui.fragments.MainFragment;

public class Navigator {
    private final Context activityContext;
    private MainFragment mainFragment;

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

    public void navigateToMainFragment() {
        if (!isFragmentAvailable(mainFragment)) {
            mainFragment = new MainFragment();
        }
        navigateToFragment(mainFragment, MainFragment.TAG);
    }

    private void navigateToFragment(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }

    private void navigateToFragmentWithBackStack(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).addToBackStack(tag).commit();
    }

    public void navigateBack() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            ((Activity) activityContext).finish();
        }
    }
}
