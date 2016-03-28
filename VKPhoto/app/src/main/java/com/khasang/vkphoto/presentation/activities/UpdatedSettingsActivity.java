package com.khasang.vkphoto.presentation.activities;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.fragments.SettingsFragment;

import java.util.List;

public class UpdatedSettingsActivity extends AppCompatPreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
        Toolbar toolbar;
        if (Build.VERSION.SDK_INT > 16) {
            setContentView(R.layout.settings_page);
            toolbar = (Toolbar) findViewById(R.id.toolbar);

        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            android.view.View content = root.getChildAt(0);
            root.removeAllViews();
            toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = toolbar.getHeight();
            }
            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(toolbar);
        }
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (Build.VERSION.SDK_INT > 16) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle(R.string.action_settings);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
