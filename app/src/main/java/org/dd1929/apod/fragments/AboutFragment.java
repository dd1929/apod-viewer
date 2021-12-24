package org.dd1929.apod.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.dd1929.apod.R;
import org.dd1929.apod.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutFragment extends Fragment {

    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;

    private final List<Map<String, Object>> mMapList = new ArrayList<>();

    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        mViewPager = v.findViewById(R.id.view_pager);
        mTabLayout = v.findViewById(R.id.tab_layout);

        mViewPager.setOffscreenPageLimit(1);

        addTabsToMapList();
        configureTabs();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity parentActivity = (AppCompatActivity) requireActivity();
        parentActivity.getSupportActionBar().setTitle(R.string.about);
        parentActivity.getSupportActionBar().setSubtitle(null);
    }

    private void addTabsToMapList(){
        Map<String, Object> aboutAPODTab = createMap(getString(R.string.about), AboutAppFragment.newInstance());
        Map<String, Object> libsAPODsTab = createMap(getString(R.string.libs_title), AboutLibsFragment.newInstance());

        mMapList.add(aboutAPODTab);
        mMapList.add(libsAPODsTab);
    }

    private void configureTabs(){
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity(), mMapList);
        mViewPager.setAdapter(viewPagerAdapter);

        if (mTabLayout != null){
            new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> tab.setText(viewPagerAdapter.getTitle(position))).attach();
        }
    }

    private Map<String, Object> createMap(String title, Fragment fragment){
        Map<String, Object> titleAndFragmentMap = new HashMap<>();

        titleAndFragmentMap.put("fragmentTitle", title);
        titleAndFragmentMap.put("fragment", fragment);

        return titleAndFragmentMap;
    }
}
