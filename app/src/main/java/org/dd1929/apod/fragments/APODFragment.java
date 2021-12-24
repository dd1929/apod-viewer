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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.dd1929.apod.dialogs.TextFormatFragment;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.database.APODViewModel;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.APODUtils;
import org.dd1929.apod.utils.DateParseUtils;
import org.dd1929.apod.utils.FormatUtils;
import org.dd1929.apod.utils.IntentUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;

public class APODFragment extends Fragment{

    private static final String BUNDLE_STATE = "SSIVState";
    private static final String ARG_DATE = "date";
    private static final String ARG_SHARE = "share";
    private static final String ARG_SET_AS = "setAs";

    private static final String TAG = "APODFragment";
    private static final String DIALOG_TEXT_FORMAT = "TextFormat";

    private View mDetailsBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GestureImageView mAltImageView;
    private SubsamplingScaleImageView mAPODImageView;
    private WebView mAPODWebView;
    private ProgressBar mProgressBar;
    private TextView mTitleTextView, mDateTextView, mCopyrightTextView, mDetailsTextView;
    private LinearLayout mTopLayout;
    private MaterialToolbar mAPODUtilsToolbar;
    private AppBarLayout mAppBarLayout;
    private ImageButton mLockButton;
    private ImageButton mExpandDetailsButton;
    private ImageButton mTextFormatButton;
    private WebSettings mWebSettings;

    private ActivityResultLauncher<String> mRequestPermissionsLauncher;
    private APODViewModel mAPODViewModel;
    private ImageViewState mState;

    private APOD mAPOD;
    private boolean isLayoutLocked = false;
    private boolean isHDImageShown = false;
    private boolean areDetailsShown = true;
    private boolean areDetailsExpanded = false;
    private GestureDetectorCompat mGestureDetectorCompat;

    public static APODFragment newInstance(Date date, boolean triggerShare, boolean triggerSetAs) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putBoolean(ARG_SHARE, triggerShare);
        args.putBoolean(ARG_SET_AS, triggerSetAs);
        APODFragment fragment = new APODFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mRequestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    APODUtils.onClickSave(mAPOD, requireActivity(), getParentFragmentManager(), requireView(), mRequestPermissionsLauncher);
                } else {
                    APODUtils.showPermissionReasonDialog(getParentFragmentManager(), requireContext());
                }
            }
        });

        mAPODViewModel = new ViewModelProvider(requireActivity()).get(APODViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_apod, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)){
            mState = (ImageViewState) savedInstanceState.getSerializable(BUNDLE_STATE);
        }

        mTopLayout = v.findViewById(R.id.top_layout);
        mAltImageView = v.findViewById(R.id.alt_image_view);
        mAPODImageView = v.findViewById(R.id.apod_image_view);
        mAPODWebView = v.findViewById(R.id.apod_web_view);
        mProgressBar = v.findViewById(R.id.progress_bar);
        mTitleTextView = v.findViewById(R.id.title_text_view);
        mDateTextView = v.findViewById(R.id.date_text_view);
        mCopyrightTextView = v.findViewById(R.id.copyright_text_view);
        mDetailsTextView = v.findViewById(R.id.details_text_view);
        mLockButton = v.findViewById(R.id.lock_button);
        mTextFormatButton = v.findViewById(R.id.text_format_button);
        mExpandDetailsButton = v.findViewById(R.id.expand_details_button);
        mAPODUtilsToolbar = v.findViewById(R.id.toolbar_apod_utils);
        mAppBarLayout = v.findViewById(R.id.app_bar_layout);

        mDetailsBottomSheet = v.findViewById(R.id.details_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mDetailsBottomSheet);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    mExpandDetailsButton.setContentDescription(getString(R.string.hide_details));
                    areDetailsExpanded = true;
                } else {
                    mExpandDetailsButton.setContentDescription(getString(R.string.show_details));
                    areDetailsExpanded = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mExpandDetailsButton.setRotation(slideOffset);
            }
        });

        setBottomSheetPeek();

        mLockButton.setOnClickListener(view -> {
            isLayoutLocked = !isLayoutLocked;
            if (isLayoutLocked){
                mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_locked));
                mLockButton.setContentDescription(getString(R.string.unlock_layout));
            } else {
                mLockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlocked));
                mLockButton.setContentDescription(getString(R.string.lock_layout));
            }
        });

        mLockButton.setOnLongClickListener(view -> {
            Toast toast = Toast.makeText(requireContext(), mLockButton.getContentDescription(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.END|Gravity.CENTER_VERTICAL, -mLockButton.getWidth(), 0);
            toast.show();
            return true;
        });

        mExpandDetailsButton.setOnClickListener(view -> {
            changeDetailsSheetState();
        });

        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), new APODGestureListener());

        mAPODImageView.setOnTouchListener((view, motionEvent) -> mGestureDetectorCompat.onTouchEvent(motionEvent));
        mAPODWebView.setOnTouchListener((view, motionEvent) -> {
            mGestureDetectorCompat.onTouchEvent(motionEvent);
            return false;
        });

        mAltImageView.getController().setOnGesturesListener(new GestureViewListener());
        mAltImageView.getController().getSettings().setMaxZoom(100f);

        mAPODImageView.setMaxScale(1f);
        mAPODImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        mAPODImageView.setMinimumDpi(1);

        mAPODUtilsToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_hd){
                if (mAPOD.getMediaType().equals("image")){
                    if (mAPOD.getHdUrl() != null){
                        isHDImageShown = !isHDImageShown;
                        setupImage();
                    }
                } else {
                    if (mAPOD.getHdUrl() == null){
                        APODUtils.makeSnackbar(getString(R.string.other_no_hd), requireView());
                    } else {
                        mAPODWebView.loadUrl(mAPOD.getHdUrl());
                        mAPODWebView.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            } else if (item.getItemId() == R.id.menu_item_view_in_browser){
                Date date = null;

                if (mAPOD != null){
                    date = new Date(mAPOD.getDate());
                }

                IntentUtils.sendWebIntent(DateParseUtils.getUrlFromDate(date), requireContext());
                return true;
            } else if (item.getItemId() == R.id.menu_item_set_as){
                if (mAPOD.getMediaType().equals("image")){
                    APODUtils.onClickSetAs(mAPOD, requireContext(), getParentFragmentManager(), requireView());
                } else {
                    APODUtils.makeSnackbar(getString(R.string.other_set_as), requireView());
                }
                return true;
            } else if (item.getItemId() == R.id.menu_item_bookmark){
                if (mAPOD != null){
                    mAPODViewModel.makeFav(mAPOD.getDate(), !mAPOD.getIsFavourite());
                }
                return true;
            } else if (item.getItemId() == R.id.menu_item_save){
                if (mAPOD.getMediaType().equals("image")){
                    APODUtils.onClickSave(mAPOD, requireActivity(), getParentFragmentManager(), requireView(), mRequestPermissionsLauncher);
                } else {
                    APODUtils.makeSnackbar(getString(R.string.other_save), requireView());
                }
                return true;
            } else if (item.getItemId() == R.id.menu_item_share){
                APODUtils.onClickShare(mAPOD, requireContext(), getParentFragmentManager(), requireView());
                return true;
            }
            return false;
        });

        mTextFormatButton.setOnClickListener(view -> { //View.OnClickListener
            FragmentManager manager = getParentFragmentManager();
            TextFormatFragment dialog = TextFormatFragment.newInstance(() -> setupTextFormat()); //TextFormatFragment.OnTextFormatChangeListener
            dialog.show(manager, DIALOG_TEXT_FORMAT);
        });

        mWebSettings = mAPODWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mAPODWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress != 100){
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebSettings.setBuiltInZoomControls(true);

        final Date date = (Date) getArguments().getSerializable(ARG_DATE);
        observeData(date);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupTextFormat();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        View rootView = getView();
        if (rootView != null){
            if (mAPODImageView.getState() != null){
                outState.putSerializable(BUNDLE_STATE, mAPODImageView.getState());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_refresh){
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void observeData(Date date) {
        mAPODViewModel.getAPOD(date).observe(getViewLifecycleOwner(), apod -> {
            mAPOD = apod;
            setupAPOD();
        });
    }

    private void setupAPOD(){
        if (isAdded()){
            if (mAPOD != null){
                setupText();
                setupMenu();

                if (mAPOD.getMediaType() != null){
                    if (mAPOD.getMediaType().equals("image")){
                        setupImage();
                    } else {
                        setupOther();
                    }
                }

                if (getArguments().getBoolean(ARG_SHARE, false)){
                    APODUtils.onClickShare(mAPOD, requireContext(), getParentFragmentManager(), requireView());
                } else if (getArguments().getBoolean(ARG_SET_AS, false)){
                    if (mAPOD.getMediaType().equals("image")){
                        APODUtils.onClickSetAs(mAPOD, requireContext(), getParentFragmentManager(), requireView());
                    } else {
                        APODUtils.makeSnackbar(getString(R.string.other_set_as), requireView());
                    }
                }
            }
        }
    }

    private void setBottomSheetPeek(){
        if (mTopLayout != null){
            mTopLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mBottomSheetBehavior.setPeekHeight(mTopLayout.getHeight());
                    mTopLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void setupMenu(){
        if (mAPOD != null) {
            if (!mAPOD.getMediaType().equals("image")){
                mAPODUtilsToolbar.getMenu().removeItem(R.id.menu_item_set_as);
                mAPODUtilsToolbar.getMenu().removeItem(R.id.menu_item_save);
            }

            if (mAPOD.getHdUrl() == null || mAPOD.getHdUrl().equals(mAPOD.getUrl()) || mAPOD.getHdUrl().equals("")){
                mAPODUtilsToolbar.getMenu().removeItem(R.id.menu_item_hd);
            }

            if (mAPOD.getIsFavourite()){
                mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_bookmark).setIcon(R.drawable.ic_bookmarked);
            } else {
                mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_bookmark).setIcon(R.drawable.ic_not_bookmarked);
            }
        }
    }

    private void setupText(){
        setupTextFormat();

        mTitleTextView.setText(mAPOD.getTitle());
        mDateTextView.setText(DateFormat.getDateFormat(getActivity()).format(new Date(mAPOD.getDate())));
        if (mAPOD.getCopyright() != null){
            mCopyrightTextView.setVisibility(View.VISIBLE);
            mCopyrightTextView.setText(getString(R.string.copyright, mAPOD.getCopyright()));
        } else {
            mCopyrightTextView.setVisibility(View.GONE);
        }
        mDetailsTextView.setText(mAPOD.getDetails());
    }

    private void setupTextFormat(){
        int textSize = AppPreferences.getPrefFontSize(getContext());
        mTitleTextView.setTextSize(textSize);
        mDateTextView.setTextSize(textSize);
        mCopyrightTextView.setTextSize(textSize);
        mDetailsTextView.setTextSize(textSize);

        Typeface type;

        switch (AppPreferences.getPrefFontType(getContext())){
            case "sans":
                type = Typeface.SANS_SERIF;
                break;
            case "serif":
                type = Typeface.SERIF;
                break;
            case "mono":
                type = Typeface.MONOSPACE;
                break;
            default: type = Typeface.DEFAULT;
        }

        mTitleTextView.setTypeface(type, Typeface.BOLD);
        mDateTextView.setTypeface(type, Typeface.NORMAL);
        mCopyrightTextView.setTypeface(type, Typeface.ITALIC);
        mDetailsTextView.setTypeface(type, Typeface.NORMAL);

        setBottomSheetPeek();
    }

    private void setupImage(){
        String url;
        if (isHDImageShown){
            url = mAPOD.getHdUrl();
        } else {
            url = mAPOD.getUrl();
        }
        String format = FormatUtils.getFileFormat(url);
        mAPODWebView.setVisibility(View.GONE);
        mAPODWebView.loadUrl("about:blank");
        mProgressBar.setVisibility(View.VISIBLE);
        if (format.equals("jpg") || format.equals("jpeg") || format.equals("png")){
            mAltImageView.setVisibility(View.GONE);
            Glide.with(this)
                    .asFile()
                    .load(url)
                    .into(new CustomTarget<File>(){
                        @Override
                        public void onResourceReady(@NonNull @NotNull File resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super File> transition) {
                            if (mState != null){
                                mAPODImageView.setImage(ImageSource.uri(Uri.fromFile(resource)), mState);
                                Log.i(TAG, "mState != null");
                            } else {
                                mAPODImageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                                Log.i(TAG, "mState == null");
                            }
                            mProgressBar.setVisibility(View.GONE);
                            mAPODImageView.setVisibility(View.VISIBLE);

                            if (mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd) != null){
                                if (isHDImageShown){
                                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setIcon(R.drawable.ic_sd);
                                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setTitle(R.string.view_normal_quality);
                                } else {
                                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setIcon(R.drawable.ic_hd);
                                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setTitle(R.string.view_high_quality);
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });
        } else {
            Glide.with(this)
                    .load(url)
                    .into(mAltImageView);
            mAltImageView.setVisibility(View.VISIBLE);
            mAPODImageView.setVisibility(View.GONE);

            if (mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd) != null){
                if (isHDImageShown){
                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setIcon(R.drawable.ic_sd);
                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setTitle(R.string.view_normal_quality);
                } else {
                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setIcon(R.drawable.ic_hd);
                    mAPODUtilsToolbar.getMenu().findItem(R.id.menu_item_hd).setTitle(R.string.view_high_quality);
                }
            }
        }
    }

    private void setupOther(){
        mAltImageView.setVisibility(View.GONE);
        mAltImageView.setImageDrawable(null);
        mAPODImageView.setVisibility(View.GONE);
        mAPODWebView.loadUrl(mAPOD.getUrl());
        mAPODWebView.setVisibility(View.VISIBLE);
    }

    private void changeDetailsSheetState(){
        if (!areDetailsExpanded){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void showDetails(){
        mDetailsBottomSheet.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.VISIBLE);
        ObjectAnimator showDetailsSheetAnimator = ObjectAnimator
                .ofFloat(mDetailsBottomSheet, "alpha", 0f, 1f)
                .setDuration(500);
        ObjectAnimator showToolbarAnimator = ObjectAnimator
                .ofFloat(mAppBarLayout, "alpha", 0f, 1f)
                .setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(showDetailsSheetAnimator).with(showToolbarAnimator);

        animatorSet.start();
    }

    private void hideDetails(){
        ObjectAnimator hideDetailsSheetAnimator = ObjectAnimator
                .ofFloat(mDetailsBottomSheet, "alpha", 1f, 0f)
                .setDuration(500);
        ObjectAnimator hideToolbarAnimator = ObjectAnimator
                .ofFloat(mAppBarLayout, "alpha", 1f, 0f)
                .setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(hideDetailsSheetAnimator).with(hideToolbarAnimator);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mDetailsBottomSheet.setVisibility(View.GONE);
                mAppBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet.start();
    }

    // SimpleOnGestureListener inner class, for mAPODImageView & mAPODWebView
    class APODGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isLayoutLocked){
                if (areDetailsShown){
                    hideDetails();
                    areDetailsShown = false;
                } else {
                    showDetails();
                    areDetailsShown = true;
                }
            }
            return true;
        }
    }

    // GestureController.SimpleOnGestureListener, for mAltImageView (GestureImageView)
    class GestureViewListener extends GestureController.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
            if (!isLayoutLocked){
                if (areDetailsShown){
                    hideDetails();
                    areDetailsShown = false;
                } else {
                    showDetails();
                    areDetailsShown = true;
                }
            }
            return true;
        }
    }
}