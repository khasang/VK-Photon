package com.khasang.vkphoto.presentation.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.activities.AppCompatPreferenceActivity;
import com.khasang.vkphoto.util.Logger;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            String page = getArguments().getString("page");
            if (page != null)
                switch (page) {
                    case "page1":
                        addPreferencesFromResource(R.xml.pref_general);
                        break;
                    case "page2":
                        addPreferencesFromResource(R.xml.pref_sync);
                        break;
                }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.settings_page, container, false);
        getActivity().setTheme(R.style.MyMaterialTheme);
        AppCompatPreferenceActivity appCompatPreferenceActivity = (AppCompatPreferenceActivity) getActivity();
        if (layout != null && !appCompatPreferenceActivity.onIsMultiPane()) {
            AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
            Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);
            ActionBar bar = activity.getSupportActionBar();
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(getPreferenceScreen().getTitle());
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() != null) {
            View frame = (View) getView().getParent();
            if (frame != null)
                frame.setPadding(0, 0, 0, 0);
        }
    }
}