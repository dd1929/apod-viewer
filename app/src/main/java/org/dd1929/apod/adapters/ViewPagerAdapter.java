package org.dd1929.apod.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends FragmentStateAdapter {

    List<Map<String, Object>> mMapList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Map<String, Object>> mapList){
        super(fragmentActivity);
        this.mMapList = mapList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (Fragment) mMapList.get(position).get("fragment");
    }

    @Override
    public int getItemCount() {
        return mMapList.size();
    }

    public String getTitle(int position){
        String title = (String) mMapList.get(position).get("fragmentTitle");
        if (title == null) title = "No Title";

        return title;
    }
}
