package org.dd1929.apod.activities;

/* APOD - A simple app to view images from NASA's APOD service
    Copyright (C) 2021  Deepto Debnath

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see https://www.gnu.org/licenses/ */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import org.dd1929.apod.fragments.APODFragment;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.models.MonthYear;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.APODUtils;
import org.dd1929.apod.utils.NetworkUtils;

import java.util.Date;
import java.util.List;

public class DetailsActivity extends AppCompatActivity{

    public static final String EXTRA_DATE = "date";
    private static final String EXTRA_SHARE = "share";
    private static final String EXTRA_SET_AS = "setAs";
    private static final String BUNDLE_DATE = "curDate";
    private static final String BUNDLE_SHARE = "triggerShare";
    private static final String BUNDLE_SET_AS = "triggerSetAs";

    private FrameLayout mFragmentContainer;

    private Date mCurDate;
    private boolean mTriggerShare = false, mTriggerSetAs = false;
    private MonthYear mCurMonthYear;
    private List<APOD> mCurAPODs;

    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private boolean isNetworkAvailable = true;

    public static Intent newIntent(Context context, Date date, boolean triggerShare, boolean triggerSetAs){
        Intent i = new Intent(context, DetailsActivity.class);
        i.putExtra(EXTRA_DATE, date);
        i.putExtra(EXTRA_SHARE, triggerShare);
        i.putExtra(EXTRA_SET_AS, triggerSetAs);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mFragmentContainer = findViewById(R.id.fragment_container);

        if (savedInstanceState != null){
            mCurDate = (Date) savedInstanceState.getSerializable(BUNDLE_DATE);
        }

        if (mCurDate == null){
            mCurDate = (Date) getIntent().getExtras().getSerializable(EXTRA_DATE);
        }

        mTriggerShare = getIntent().getExtras().getBoolean(EXTRA_SHARE, false);
        mTriggerSetAs = getIntent().getExtras().getBoolean(EXTRA_SET_AS, false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_container, APODFragment.newInstance(mCurDate, mTriggerShare, mTriggerSetAs)).commit();

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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_apod, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_DATE, mCurDate);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        super.onDestroy();
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
