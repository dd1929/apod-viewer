package org.dd1929.apod.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.dd1929.apod.fragments.MonthlyFragment;
import org.dd1929.apod.models.MonthYear;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MonthlyAdapter extends FragmentStateAdapter {

    private static final String TAG = "MonthlyAdapter";

    private List<MonthYear> mMonthYears;

    public MonthlyAdapter(@NonNull @NotNull FragmentActivity fragmentActivity, List<MonthYear> monthYears) {
        super(fragmentActivity);
        mMonthYears = monthYears;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return MonthlyFragment.newInstance(mMonthYears.get(position));
    }

    @Override
    public int getItemCount() {
        if (mMonthYears != null){
            return mMonthYears.size();
        } else {
            return 0;
        }
    }
}
