package org.dd1929.apod.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.dd1929.apod.adapters.APODAdapter;
import org.dd1929.apod.database.APODViewModel;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.R;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.utils.APODUtils;
import org.dd1929.apod.utils.DateParseUtils;
import org.dd1929.apod.utils.IntentUtils;
import org.dd1929.apod.viewholders.APODHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ACTION_OPEN_BROWSER = "openInBrowser";
    public static final String ACTION_SET_AS = "setAs";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_SHARE = "share";

    private LinearLayout mDiscoverEmptyView;
    private Button mDiscoverButton;
    private RecyclerView mRecyclerView;
    private APODAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mProgressBar;

    private ActivityResultLauncher<String> mRequestPermissionsLauncher;
    private APODViewModel mAPODViewModel;

    private List<APOD> mAPODs = new ArrayList<>();
    private APOD mSaveAPOD = null;

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mRequestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    if (mSaveAPOD != null){
                        APODUtils.onClickSave(mSaveAPOD, requireActivity(), getParentFragmentManager(), requireView(), mRequestPermissionsLauncher);
                    }
                } else {
                    APODUtils.showPermissionReasonDialog(getParentFragmentManager(), requireContext());
                }
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mAPODViewModel = new ViewModelProvider(requireActivity()).get(APODViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover, container, false);

        mRecyclerView = v.findViewById(R.id.apod_grid_view);
        mSwipeRefresh = v.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefresh.setColorSchemeResources(R.color.color_primary);
        mSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.swipe_refresh_color_background);

        mDiscoverEmptyView = v.findViewById(R.id.discover_empty_view);
        mDiscoverButton = v.findViewById(R.id.discover_button);
        mProgressBar = v.findViewById(R.id.progress_bar);

        mDiscoverButton.setOnClickListener(view -> {
            mAPODViewModel.refreshRandomAPODs(AppPreferences.getPrefRandomCount(requireContext()));
            mProgressBar.setVisibility(View.VISIBLE);
            mDiscoverEmptyView.setVisibility(View.GONE);
        });

        if (AppPreferences.getPrefView(getActivity()).equals("list")){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            int cols;
            if (AppPreferences.getPrefNumColumns(getActivity()) <= 0){
                cols = getResources().getInteger(R.integer.number_of_columns);
            } else {
                cols = AppPreferences.getPrefNumColumns(getActivity());
            }
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), cols));
        }

        setupAdapter();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setScreenTitle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_refresh){
            refresh();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void setAPODs(List<APOD> apods){
        mAPODs.clear();
        mAPODs.addAll(apods);
        if (mAPODs != null && mAdapter != null){
            APODUtils.setAPODsOnAdapter(requireContext(), mAPODs, mAdapter);
        }
    }

    private void refresh(){
        mSwipeRefresh.setRefreshing(true);
        mAPODViewModel.refreshRandomAPODs(AppPreferences.getPrefRandomCount(requireContext()));
        setupAdapter();
    }

    protected void setupAdapter(){
        if (mRecyclerView.getAdapter() == null){
            mAdapter = new APODAdapter(new APODHolder.Callbacks() {
                @Override
                public void onFavChanged(long date, boolean isFav) {
                    mAPODViewModel.makeFav(date, isFav);
                }

                @Override
                public void onActionInvoked(APOD apod, String action) {
                    switch (action){
                        case ACTION_OPEN_BROWSER:
                            IntentUtils.sendWebIntent(DateParseUtils.getUrlFromDate(new Date(apod.getDate())), requireContext());
                            break;
                        case ACTION_SET_AS:
                            APODUtils.onClickSetAs(apod, requireContext(), getParentFragmentManager(), requireView());
                            break;
                        case ACTION_SAVE:
                            mSaveAPOD = apod;
                            APODUtils.onClickSave(apod, requireActivity(), getParentFragmentManager(), requireView(), mRequestPermissionsLauncher);
                            break;
                        case ACTION_SHARE:
                            APODUtils.onClickShare(apod, requireContext(), getParentFragmentManager(), requireView());
                            break;
                    }
                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }

        setScreenTitle();
        if (mRecyclerView.getAdapter() != null){
            mAPODViewModel.getRandomAPODs().observe(getViewLifecycleOwner(), apods -> { //Observer<List<APOD>>
                if (apods != null){
                    if (apods.size() != 0){
                        setAPODs(apods);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mDiscoverEmptyView.setVisibility(View.GONE);
                        mSwipeRefresh.setRefreshing(false);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        mDiscoverEmptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mDiscoverEmptyView.setVisibility(View.GONE);
                }
            });
        }

        mSwipeRefresh.setRefreshing(false);
    }

    private void setScreenTitle(){
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        if (actionBar == null){
            return;
        }

        actionBar.setTitle(getString(R.string.discover));
        actionBar.setSubtitle(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s){
            case "sortBy":
            case "sortOrder":
            case "filterMediaType":
            case "filterCopyright":
                if (mAPODs != null && mAdapter != null){
                    APODUtils.setAPODsOnAdapter(requireContext(), mAPODs, mAdapter);
                }
        }
    }
}
