package com.khasang.vkphoto.presentation.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.afollestad.materialdialogs.AlertDialogWrapper;
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
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
    private View mLayout;


    public void showContacts(View v) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");
//            showContactDetails();
        }
    }

    private void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.st_btn_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

//    private void showContactDetails() {
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
//                .addToBackStack("contacts")
//                .commit();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

//        if (requestCode == REQUEST_CAMERA) {
//            // BEGIN_INCLUDE(permission_result)
//            // Received permission result for camera permission.
//            Log.i(TAG, "Received response for Camera permission request.");
//
//            // Check if the only required permission has been granted
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Camera permission has been granted, preview can be displayed
//                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
//                Snackbar.make(mLayout, R.string.permision_available_camera,
//                        Snackbar.LENGTH_SHORT).show();
//            } else {
//                Log.i(TAG, "CAMERA permission was NOT granted.");
//                Snackbar.make(mLayout, R.string.permissions_not_granted,
//                        Snackbar.LENGTH_SHORT).show();
//
//            }
//            // END_INCLUDE(permission_result)
//
//        } else
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            Log.i(TAG, "Received response for contact permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (checkPermission())
//        verifyStoragePermissions();
        {
            setContentView(R.layout.activity_main);
            mLayout = findViewById(R.id.sample_main_layout);
            initServiceConnection(savedInstanceState);
            loginVk();
            initViews();
            initViewPager();
            if (savedInstanceState != null) {
                Navigator.changeViewPagerVisibility(this, savedInstanceState.getBoolean(VIEWPAGER_VISIBLE));
            }
            measureScreen();
        }
    }

//    private boolean checkPermission() {
//        boolean isPermissionApply = true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
////                // Explain to the user why we need to read the contacts
////                new AlertDialogWrapper.Builder(getApplicationContext())
////                        .setTitle(R.string.title)
////                        .setMessage(R.string.request_write_permission)
////                        .setNegativeButton(R.string.st_btn_ok, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.dismiss();
////
//////                            ActivityCompat.requestPermissions( ((Activity) context,
//////                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//////                                    Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
////                            }
////                        }).show();
//                isPermissionApply = true;
//            } else {
////                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                    Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
////                isPermissionApply = false;
//            }
////            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 29025);
//            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE (29025) is an app-defined int constant
//        }
//        return isPermissionApply;
//    }

//    public void verifyStoragePermissions() {
//        // Check if we have write permission
//        int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            requestPermissions(
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_WRITE_EXTERNAL_STORAGE
//            );
//        }
//    }

//    private static void requestPermission(final Context context){
//        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            new AlertDialogWrapper.Builder(context)
//                    .setTitle(R.string.title)
//                    .setMessage(R.string.request_write_permission)
//                    .setNegativeButton(R.string.st_btn_ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
////                            ActivityCompat.requestPermissions( ((Activity) context,
////                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                                    Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
//                        }
//                    }).show();
//
//        }else {
//            // permission has not been granted yet. Request it directly.
//            ActivityCompat.requestPermissions((Activity)context,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
//        }
//    }

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
                if (VKAccessToken.currentToken() != null && viewPager.getVisibility() == View.VISIBLE && savedInstanceState == null) {
                    EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
                    syncService.startSync();
                    Logger.d("ViewPagerVisibile" + viewPager.getVisibility());
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
