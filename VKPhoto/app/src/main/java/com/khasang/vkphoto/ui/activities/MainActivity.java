package com.khasang.vkphoto.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.database.MySQliteHelper;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.services.SyncService;
import com.khasang.vkphoto.services.SyncServiceImpl;
import com.khasang.vkphoto.ui.fragments.VkAlbumsFragment;
import com.khasang.vkphoto.ui.fragments.GalleryFragment;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SyncServiceProvider {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ServiceConnection sConn;
    private boolean bound = false;
    private Intent intent;
    private SyncService syncService;
    private Navigator navigator;
    private final String[] scopes = {VKScope.WALL, VKScope.PHOTOS};
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigator();
        initServiceConnection();
        initViews();
        VKSdk.login(this, scopes);
        MySQliteHelper mySQliteHelper = MySQliteHelper.getInstance(this);
        mySQliteHelper.getWritableDatabase();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VkAlbumsFragment(), "VK Albums");
        adapter.addFragment(new GalleryFragment(), "Gallery Albums");
        viewPager.setAdapter(adapter);
    }

    private void initNavigator() {
        navigator = new Navigator(this);
        navigator.navigateToMainFragment();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ToastUtils.showShortMessage("Undone", getApplicationContext());
                            }
                        }).show();
            }
        });
    }

    private void initServiceConnection() {
        intent = new Intent(getApplicationContext(), SyncServiceImpl.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Logger.d("MainActivity onServiceConnected");
                syncService = ((SyncServiceImpl.MyBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(intent, sConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(MainActivity.this, "Authorized", Toast.LENGTH_SHORT).show();
                // User passed Authorization
            }

            @Override
            public void onError(VKError error) {
                finish();
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public SyncService getSyncService() {
        return syncService;
    }

    @Override
    public void onBackPressed() {
        navigator.navigateBack();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
