package org.dd1929.apod.dialogs;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.dd1929.apod.models.Library;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.IntentUtils;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class LicenseFragment extends DialogFragment {

    private static final String ARG_LIB = "library";

    private TextView mTitleTextView, mAuthorTextView, mDescTextView;
    private TextView mCopyrightTextView, mLicenseTextView;
    private Button mViewFullLicenseButton;

    public static LicenseFragment newInstance(Library lib){
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIB, lib);

        LicenseFragment fragment = new LicenseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_license, null);

        Library library = (Library) getArguments().getSerializable(ARG_LIB);

        mTitleTextView = v.findViewById(R.id.title_text_view);
        mAuthorTextView = v.findViewById(R.id.author_text_view);
        mDescTextView = v.findViewById(R.id.desc_text_view);
        mCopyrightTextView = v.findViewById(R.id.copyright_text_view);
        mLicenseTextView = v.findViewById(R.id.short_license_text_view);
        mViewFullLicenseButton = v.findViewById(R.id.view_full_license_btn);

        mLicenseTextView.setMovementMethod(BetterLinkMovementMethod.getInstance());

        mTitleTextView.setText(library.getName());
        mAuthorTextView.setText(library.getAuthor());
        mDescTextView.setText(library.getDescription());
        mCopyrightTextView.setText(library.getCopyright());

        switch (library.getLicense()){
            case APACHEv2:
                setLicenseText(R.string.apache_v2);
                mViewFullLicenseButton.setVisibility(View.VISIBLE);
                setupLicenseButton(R.string.apache_v2_url);
                break;
            case GPLv3:
                setLicenseText(R.string.gpl_v3);
                mViewFullLicenseButton.setVisibility(View.VISIBLE);
                setupLicenseButton(R.string.gpl_v3_url);
                break;
            case BSD:
                switch (library.getName()){
                    case "Glide":
                        mLicenseTextView.setText(getString(R.string.bsd_2, "GOOGLE, INC", "Google, Inc."));
                        mViewFullLicenseButton.setVisibility(View.VISIBLE);
                        setupLicenseButton(R.string.glide_license_url);
                        break;
                }

                break;
            case MIT:
                setLicenseText(R.string.mit);
                mViewFullLicenseButton.setVisibility(View.GONE);
        }
        
        return v;
    }

    private void setLicenseText(int licenseText){
        mLicenseTextView.setText(getString(licenseText));
    }

    private void setupLicenseButton(int licenseUrl){
        mViewFullLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.sendWebIntent(Uri.parse(getString(licenseUrl)), getContext());
            }
        });
    }
}
