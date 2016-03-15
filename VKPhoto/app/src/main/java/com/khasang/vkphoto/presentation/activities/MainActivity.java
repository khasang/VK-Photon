package com.khasang.vkphoto.presentation.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.events.CloseActionModeEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.fragments.AlbumsFragment;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumsFragment;
import com.khasang.vkphoto.ui.activities.SettingsActivity;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SyncServiceProvider, FabProvider {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static String VIEWPAGER_VISIBLE = "viewpager_visible";
    private final String[] scopes = {VKScope.WALL, VKScope.PHOTOS};
    private ServiceConnection sConn;
    private boolean bound = false;
    private Intent intent;
    private SyncService syncService;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    public static int ALBUM_THUMB_HEIGHT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initServiceConnection(savedInstanceState);
        loginVk();
        initViews();
        initViewPager();
        if (savedInstanceState != null) {
            Navigator.changeViewPagerVisibility(this, savedInstanceState.getBoolean(VIEWPAGER_VISIBLE));
        }
        calculateAlbumThumbHeight();
    }

    private void calculateAlbumThumbHeight(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int thumbWidth;
        int paddingsLR = 60 * 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            thumbWidth = (screenWidth - paddingsLR - 50) / 2;
        else
            thumbWidth = screenWidth - paddingsLR;
        ALBUM_THUMB_HEIGHT = Math.round(thumbWidth / 16 * 9);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Navigator.setTabPosition(position);
                EventBus.getDefault().post(new CloseActionModeEvent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loginVk() {
        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(this, scopes);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlbumsFragment(), "VK Albums");
        adapter.addFragment(new LocalAlbumsFragment(), "Gallery Albums");
        viewPager.setAdapter(adapter);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initServiceConnection(final Bundle savedInstanceState) {
        intent = new Intent(getApplicationContext(), SyncServiceImpl.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Logger.d("MainActivity onServiceConnected");
                syncService = ((SyncServiceImpl.MyBinder) binder).getService();
                bound = true;
                if (VKAccessToken.currentToken() != null && viewPager.getVisibility() == View.VISIBLE && savedInstanceState == null) {
                    Logger.d("ViewPagerVisibile" + viewPager.getVisibility());
                    EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
                }
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
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
                if (sConn != null) {
                    EventBus.getDefault().post(new SyncAndTokenReadyEvent());
                }
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
    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    @Override
    public void onBackPressed() {
        Navigator.navigateBack(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(VIEWPAGER_VISIBLE, viewPager.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(outState);
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
