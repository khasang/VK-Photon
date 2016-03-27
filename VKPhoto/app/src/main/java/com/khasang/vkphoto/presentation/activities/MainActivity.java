package com.khasang.vkphoto.presentation.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.events.CloseActionModeEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.domain.services.SyncService;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.fragments.AlbumsFragment;
import com.khasang.vkphoto.presentation.fragments.LocalAlbumsFragment;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.FileManager;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.PermissionUtils;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SyncServiceProvider, FabProvider, SearchView.OnQueryTextListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static int ALBUM_THUMB_HEIGHT = 0;
    public static int PHOTOS_COLUMNS = 0;
    private static String VIEWPAGER_VISIBLE = "viewpager_visible";
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    private static Fragment localAlbumsFragment, albumsFragment;
    private final String[] scopes = {VKScope.WALL, VKScope.PHOTOS};
    private ServiceConnection sConn;
    private boolean bound = false;
    private Intent intent;
    private SyncService syncService;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private ViewPagerAdapter adapter;

    private static String[] PERMISSIONS_EXTERNAL = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.activity_main);
            initServiceConnection(savedInstanceState);
            loginVk();
            initViews();
            if (Build.VERSION.SDK_INT < 23) {
                codeForRequestPermission();
            } else if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionUtils.isPermissionsGranted(this)) {
                    codeForRequestPermission();
                }
            }
            if (savedInstanceState != null) {
                Navigator.changeViewPagerVisibility(this, savedInstanceState.getBoolean(VIEWPAGER_VISIBLE));
                getSupportActionBar().setTitle(savedInstanceState.getString(ACTION_BAR_TITLE));
            }
            measureScreen();
        }
    }

    private void codeForRequestPermission() {
        initViewPager();
        if (!FileManager.initBaseDirectory(getApplicationContext())) {
            throw new RuntimeException("Base directory was not created");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSIONS) {
            Logger.d("Received response for permissions request.");
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Logger.d("Permissions has now been granted.");
                codeForRequestPermission();
            } else if (grantResults.length != 2 && grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Logger.d("Permissions was NOT granted.");
            }
        } else if (requestCode != Constants.REQUEST_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            finish();
        }
    }

    private void measureScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        float density = metrics.density;
        int thumbWidth;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            thumbWidth = (int) (screenWidth - 50 * density) / 2;
        else
            thumbWidth = screenWidth;
        ALBUM_THUMB_HEIGHT = Math.round(thumbWidth / 16 * 9);
        PHOTOS_COLUMNS = (int) (screenWidth / (90 * density));
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment item = adapter.getItem(position);
                String tag = item.getTag();
                Navigator.setTabTag(tag);
                EventBus.getDefault().post(new CloseActionModeEvent());
                fab.show();
                Logger.d("onPageSelected" + tag);
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
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(supportFragmentManager);
        if (localAlbumsFragment == null) {
            albumsFragment = new AlbumsFragment();
            localAlbumsFragment = new LocalAlbumsFragment();
        }
        adapter.addFragment(albumsFragment, "VK Albums");
        adapter.addFragment(localAlbumsFragment, "Gallery Albums");
        viewPager.setAdapter(adapter);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Navigator.initToolbar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initServiceConnection(final Bundle savedInstanceState) {
        intent = new Intent(getApplicationContext(), SyncServiceImpl.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Logger.d("MainActivity onServiceConnected");
                syncService = ((SyncServiceImpl.MyBinder) binder).getService();
                bound = true;
                if (VKAccessToken.currentToken() != null && savedInstanceState == null) {
                    EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
                    syncService.startSync();
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

//    private void startMode(Mode mode) {
//        if(mode == Mode.START){
//            searchMenuItem.setVisible(true);
//            microMenuItem.setVisible(false);
//            toolbar.setTitle(getString(R.string.app_name));
//        }else if(mode == Mode.ALBUM){
////            toolbar.setTitle(getString());
//        }
//    }

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
        if (id == R.id.log_out) {
            new MaterialDialog.Builder(this)
                    .title(R.string.logout)
                    .positiveText(R.string.st_btn_ok)
                    .negativeText(R.string.st_btn_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        syncService.deleteAllVkPhotoAlbums();
                                        VKSdk.logout();
                                        finish();
                                    }
                                }
                    ).show();
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
        if (viewPager != null) {
            outState.putBoolean(VIEWPAGER_VISIBLE, viewPager.getVisibility() == View.VISIBLE);
        }
        outState.putString(ACTION_BAR_TITLE, getSupportActionBar().getTitle().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
