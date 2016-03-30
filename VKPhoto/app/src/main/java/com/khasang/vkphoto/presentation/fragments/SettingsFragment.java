package com.khasang.vkphoto.presentation.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.khasang.vkphoto.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private int summaryColor;

    @SuppressLint("NewApi")
    @TargetApi(23)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        addPreferencesFromResource(R.xml.preferences);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            summaryColor = getResources().getColor(R.color.colorPrimary, getActivity().getTheme());
        } else {
            summaryColor = getResources().getColor(R.color.colorPrimary);
        }
        initSummary(getPreferenceScreen());
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(getColoredString(listPref.getEntry()));
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password")) {
                p.setSummary(getColoredString("*****"));
            } else {
                p.setSummary(getColoredString(editTextPref.getText()));
            }
        }
    }

    private CharSequence getColoredString(CharSequence sequence) {
        Spannable summary = new SpannableString(sequence);
        summary.setSpan(new ForegroundColorSpan(summaryColor), 0, summary.length(), 0);
        return summary;
//        return Html.fromHtml("<font color=\"" + summaryColor + "\">" + sequence + "</font>");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
    }
}
