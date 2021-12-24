package org.dd1929.apod.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.dd1929.apod.database.APODViewModel;
import org.dd1929.apod.dialogs.TextFormatFragment;
import org.dd1929.apod.R;
import org.dd1929.apod.executors.FileOpsExecutor;
import org.dd1929.apod.workers.APODUpdateWorker;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.utils.APODUtils;

import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";
    private static final String DIALOG_TEXT_FORMAT = "TextFormat";
    private static final String DIALOG_WALLPAPER_PROMPT = "AutoWallpaper";

    private APODViewModel mAPODViewModel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mAPODViewModel = new ViewModelProvider(requireActivity()).get(APODViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity parentActivity = (AppCompatActivity) requireActivity();
        parentActivity.getSupportActionBar().setTitle(R.string.settings);
        parentActivity.getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference mWallpaperTargetPref = findPreference("wallpaperTarget");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            getPreferenceScreen().removePreference(mWallpaperTargetPref);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (getString(R.string.text_formatting).equals(preference.getTitle())) {
            FragmentManager manager = getParentFragmentManager();
            TextFormatFragment dialog = TextFormatFragment.newInstance(() ->{}); //TextFormatFragment.OnTextFormatChangeListener
            dialog.show(manager, DIALOG_TEXT_FORMAT);
            return true;
        } else if (getString(R.string.clear_database).equals(preference.getTitle())){
            APODUtils.makeSnackbar(getString(R.string.clearing_database), getView());
            mAPODViewModel.getAllAPODs().observe(getViewLifecycleOwner(), apods -> {
                if (apods != null){
                    if (apods.isEmpty()){
                        APODUtils.makeSnackbar(getString(R.string.clear_database_successful), getView());
                    }
                }
            });
            mAPODViewModel.deleteAll();
            return true;
        } else if (getString(R.string.clear_image_cache).equals(preference.getTitle())){
            APODUtils.makeSnackbar(getString(R.string.clearing_image_cache), getView());
            FileOpsExecutor.getInstance()
                    .clearGlideCache(requireContext().getApplicationContext(), (isSuccessful, file) -> {
                        if (isSuccessful){
                            APODUtils.makeSnackbar(getString(R.string.clear_image_cache_successful), getView());
                        }
                    });
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s){
            case "theme":
                if (AppPreferences.getPrefTheme(getActivity()).equals("dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (AppPreferences.getPrefTheme(getActivity()).equals("light")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            case "updates":
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                final PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(APODUpdateWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
                if (!AppPreferences.getPrefBgUpdates(getActivity()).equals(false)){
                    WorkManager.getInstance(getActivity()).enqueue(workRequest);
                } else {
                    WorkManager.getInstance(getActivity()).cancelWorkById(workRequest.getId());
                }
        }
    }
}
