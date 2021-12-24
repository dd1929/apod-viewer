package org.dd1929.apod.fragments;

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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.dd1929.apod.BuildConfig;
import org.dd1929.apod.dialogs.LicenseFragment;
import org.dd1929.apod.models.Library;
import org.dd1929.apod.enums.License;
import org.dd1929.apod.R;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class AboutAppFragment extends Fragment{
    private static final String DIALOG_LICENSE = "license";

    private Library mLibrary;
    private Button mMoreAPODBtn, mMainSiteBtn, mAPODContributeBtn, mFaqButton;
    private Button mAppLicenseBtn, mAppSourceBtn, mAppContributeBtn;
    private Button mGithubButton, mEmailButton, mTelegramButton;
    private TextView mVersionTextView;
    private TextView mImgLicenseTextView;

    public static AboutAppFragment newInstance() {
        return new AboutAppFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_app, container, false);

        mMoreAPODBtn = v.findViewById(R.id.more_about_apod_btn);
        mMainSiteBtn = v.findViewById(R.id.main_site_btn);
        mAPODContributeBtn = v.findViewById(R.id.contribute_apod_btn);
        mFaqButton = v.findViewById(R.id.faq_btn);
        mAppLicenseBtn = v.findViewById(R.id.view_license_btn);
        mAppSourceBtn = v.findViewById(R.id.view_source_btn);
        mAppContributeBtn = v.findViewById(R.id.contribute_app_btn);
        mGithubButton = v.findViewById(R.id.github_btn);
        mEmailButton = v.findViewById(R.id.email_btn);
        mTelegramButton = v.findViewById(R.id.telegram_btn);
        mVersionTextView = v.findViewById(R.id.version_text_view);
        mImgLicenseTextView = v.findViewById(R.id.img_license_text_view);
        mLibrary = new Library(getString(R.string.app_name), getString(R.string.author), getString(R.string.app_desc), getString(R.string.app_cr), License.GPLv3, getString(R.string.gpl_v3_url), getString(R.string.app_src), null);

        mMoreAPODBtn.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.about_apod_url))));

        mMainSiteBtn.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.main_site_url))));

        mAPODContributeBtn.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.apod_contribute_url))));

        mFaqButton.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.apod_faq_url))));

        mAppLicenseBtn.setOnClickListener(view -> {
            FragmentManager manager = getParentFragmentManager();
            LicenseFragment dialog = LicenseFragment.newInstance(mLibrary);
            dialog.show(manager, DIALOG_LICENSE);
        });

        mAppSourceBtn.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.app_src))));

        mAppContributeBtn.setOnClickListener(view -> {

        });

        mGithubButton.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.author_gh_url))));

        mEmailButton.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.author_email_url))));

        mTelegramButton.setOnClickListener(view -> sendWebIntent(Uri.parse(getString(R.string.author_tg_url))));

        mVersionTextView.setText(getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

        mImgLicenseTextView.setText(Html.fromHtml(getString(R.string.img_license_text)));
        mImgLicenseTextView.setMovementMethod(BetterLinkMovementMethod.getInstance());

        return v;
    }

    private void sendWebIntent(Uri url){
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(url);
        startActivity(webIntent);
    }
}
