package org.dd1929.apod.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;

import org.dd1929.apod.dialogs.APODFilterFragment;
import org.dd1929.apod.dialogs.APODSortFragment;
import org.dd1929.apod.dialogs.DatePickerFragment;
import org.dd1929.apod.fragments.AboutFragment;
import org.dd1929.apod.fragments.BookmarksFragment;
import org.dd1929.apod.fragments.DiscoverFragment;
import org.dd1929.apod.fragments.RecentFragment;
import org.dd1929.apod.fragments.SettingsFragment;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.APODUtils;
import org.dd1929.apod.utils.NetworkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class HomeScreenActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, NavigationBarView.OnItemReselectedListener{

    private static final String DIALOG_DATE = "dateDialog";
    private static final String DIALOG_SORT = "sortDialog";
    private static final String DIALOG_FILTER = "filterDialog";
    private static final String BUNDLE_FRAGMENT = "CurrentFragment";
    private static final String RECENT_FRAGMENT = "MonthlyFragment";
    private static final String BOOKMARKS_FRAGMENT = "BookmarksFragment";
    private static final String DISCOVER_FRAGMENT = "DiscoverFragment";
    private static final String ABOUT_FRAGMENT = "AboutAppFragment";
    private static final String SETTINGS_FRAGMENT = "SettingsFragment";

    private MaterialToolbar mToolbar;
    private NavigationBarView mNavigationBarView;
    private FrameLayout mFragmentContainer;

    private String mCurFragment;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private boolean isNetworkAvailable = true;

    public static Intent newIntent(Context context){
        Intent i = new Intent(context, HomeScreenActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        String curFragment = null;
        if (savedInstanceState != null){
            curFragment = savedInstanceState.getString(BUNDLE_FRAGMENT);
        }

        mToolbar = findViewById(R.id.toolbar);
        mNavigationBarView = findViewById(R.id.navigation_view);
        mFragmentContainer = findViewById(R.id.fragment_container);

        if (getSupportActionBar() == null){
            setSupportActionBar(mToolbar);
        }

        mNavigationBarView.setOnItemSelectedListener(this);
        mNavigationBarView.setOnItemReselectedListener(this);
        mNavigationBarView.setSelectedItemId(R.id.menu_item_recent);

        if (curFragment != null){
            switch (curFragment){
                case RECENT_FRAGMENT:
                    transaction.replace(R.id.fragment_container, RecentFragment.newInstance()).commit();
                    mCurFragment = RECENT_FRAGMENT;
                    break;
                case DISCOVER_FRAGMENT:
                    transaction.replace(R.id.fragment_container, DiscoverFragment.newInstance()).commit();
                    mCurFragment = DISCOVER_FRAGMENT;
                    break;
                case BOOKMARKS_FRAGMENT:
                    transaction.replace(R.id.fragment_container, BookmarksFragment.newInstance()).commit();
                    mCurFragment = BOOKMARKS_FRAGMENT;
                    break;
                case SETTINGS_FRAGMENT:
                    transaction.replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                    mCurFragment = SETTINGS_FRAGMENT;
                    break;
                case ABOUT_FRAGMENT:
                    transaction.replace(R.id.fragment_container, AboutFragment.newInstance()).commit();
                    mCurFragment = ABOUT_FRAGMENT;
                    break;
            }
        } else {
            transaction.replace(R.id.fragment_container, RecentFragment.newInstance()).commit();
            mCurFragment = RECENT_FRAGMENT;
        }

        mNetworkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                checkInternet();
            }

            @Override
            public void onLost(@NonNull Network network) {
                checkInternet();
            }
        };
        mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        mConnectivityManager.registerNetworkCallback(request, mNetworkCallback);

        checkInternet();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu for all RecyclerViewFragments (but not SettingsFragment)
        getMenuInflater().inflate(R.menu.menu_fragment_home_screen, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurFragment.equals(SETTINGS_FRAGMENT) || mCurFragment.equals(ABOUT_FRAGMENT)){
            menu.findItem(R.id.menu_item_sort).setVisible(false);
            menu.findItem(R.id.menu_item_filter).setVisible(false);
            menu.findItem(R.id.menu_item_refresh).setVisible(false);
            return true;
        } else if (mCurFragment.equals(RECENT_FRAGMENT) || mCurFragment.equals(BOOKMARKS_FRAGMENT)){
            menu.findItem(R.id.menu_item_refresh).setVisible(false);
            return true;
        } else {
            return super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_sort){
            FragmentManager manager = getSupportFragmentManager();
            APODSortFragment dialog = APODSortFragment.newInstance();
            dialog.show(manager, DIALOG_SORT);
            return true;
        } else if (item.getItemId() == R.id.menu_item_filter){
            FragmentManager manager = getSupportFragmentManager();
            APODFilterFragment dialog = APODFilterFragment.newInstance();
            dialog.show(manager, DIALOG_FILTER);
            return true;
        } else if (item.getItemId() == R.id.menu_item_date){
            FragmentManager manager = getSupportFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(Calendar.getInstance().getTime(),
                    date -> { //DatePickerFragment.OnDateSelectedListener
                        Intent detailsIntent = DetailsActivity.newIntent(this, date, false, false);
                        startActivity(detailsIntent);
                    });
            dialog.show(manager, DIALOG_DATE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putString(BUNDLE_FRAGMENT, mCurFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        if (item.getItemId() == R.id.menu_item_recent) {
            transaction.replace(R.id.fragment_container, RecentFragment.newInstance()).commit();
            mCurFragment = RECENT_FRAGMENT;
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.menu_item_discover) {
            transaction.replace(R.id.fragment_container, DiscoverFragment.newInstance()).commit();
            mCurFragment = DISCOVER_FRAGMENT;
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.menu_item_bookmarks){
            transaction.replace(R.id.fragment_container, BookmarksFragment.newInstance()).commit();
            mCurFragment = BOOKMARKS_FRAGMENT;
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.menu_item_settings){
            transaction.replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
            mCurFragment = SETTINGS_FRAGMENT;
            invalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.menu_item_about) {
            transaction.replace(R.id.fragment_container, AboutFragment.newInstance()).commit();
            mCurFragment = ABOUT_FRAGMENT;
            invalidateOptionsMenu();
            return true;
        }
        return false;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }

    private void checkInternet(){
        if (NetworkUtils.hasInternet(getApplicationContext()) && !isNetworkAvailable){
            APODUtils.makeSnackbar(getString(R.string.yes_network_message), mFragmentContainer);
            isNetworkAvailable = true;
        } else if (!NetworkUtils.hasInternet(getApplicationContext()) && isNetworkAvailable){
            APODUtils.makeSnackbar(getString(R.string.no_network_message), mFragmentContainer);
            isNetworkAvailable = false;
        }
    }
}
