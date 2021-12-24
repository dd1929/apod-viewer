package org.dd1929.apod.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;

import org.dd1929.apod.adapters.MonthlyAdapter;
import org.dd1929.apod.dialogs.MonthYearFragment;
import org.dd1929.apod.models.MonthYear;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.FormatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecentFragment extends Fragment{

    private static final String TAG = "RecentFragment";
    private static final String DIALOG_MONTH_YEAR = "monthYearDialog";

    private Button mMonthYearButton;
    private MaterialToolbar mMonthNavigationBar;
    private ViewPager2 mMonthViewPager;
    private MonthlyAdapter mMonthlyAdapter;
    private MonthChangeCallback mMonthChangeCallback;

    private MonthYear mMonthYear;
    private List<MonthYear> mMonthYears;

    public static RecentFragment newInstance() {
        return new RecentFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recent, container, false);

        mMonthYearButton = v.findViewById(R.id.month_year_button);
        mMonthNavigationBar = v.findViewById(R.id.month_navigation_bar);
        mMonthViewPager = v.findViewById(R.id.view_pager);

        mMonthYearButton.setOnClickListener(view -> {
            FragmentManager manager = getParentFragmentManager();
            MonthYearFragment dialog = MonthYearFragment.newInstance((monthYear) -> {
                mMonthYear = monthYear;
                selectCurMonth();
            });
            dialog.show(manager, DIALOG_MONTH_YEAR);
        });

        mMonthNavigationBar.setOnMenuItemClickListener(item -> {
            if (mMonthViewPager == null){
                return false;
            }

            if (item.getItemId() == R.id.menu_item_oldest){
                mMonthViewPager.setCurrentItem(mMonthYears.size() - 1, true);
                return true;
            } else if (item.getItemId() == R.id.menu_item_older){
                int olderItem = mMonthViewPager.getCurrentItem() + 1;
                if (olderItem <= (mMonthYears.size() - 1)){
                    mMonthViewPager.setCurrentItem(olderItem, true);
                    return true;
                }
                return false;
            } else if (item.getItemId() == R.id.menu_item_newer){
                int newerItem = mMonthViewPager.getCurrentItem() - 1;
                if (newerItem >= 0){
                    mMonthViewPager.setCurrentItem(newerItem, true);
                    return true;
                }
                return false;
            } else if (item.getItemId() == R.id.menu_item_newest){
                mMonthViewPager.setCurrentItem(0, true);
                return true;
            } else {
                return false;
            }
        });

        mMonthChangeCallback = new MonthChangeCallback();
        mMonthViewPager.registerOnPageChangeCallback(mMonthChangeCallback);

        mMonthYear = MonthYear.fromString(AppPreferences.getPrefMonthYear(requireContext()));
        if (mMonthYear == null){
            mMonthYear = new MonthYear(Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.YEAR));
        }

        mMonthYears = new ArrayList<>();
        for (int year = Calendar.getInstance().get(Calendar.YEAR); year >= 1995; year--){
            if (year == 1995){
               for (int month = Calendar.DECEMBER; month >= Calendar.JUNE; month--){
                   mMonthYears.add(new MonthYear(month, year));
               }
            } else if (year == Calendar.getInstance().get(Calendar.YEAR)){
                for (int month = Calendar.getInstance().get(Calendar.MONTH); month >= Calendar.JANUARY; month--){
                    mMonthYears.add(new MonthYear(month, year));
                }
            } else {
                for (int month = Calendar.DECEMBER; month >= Calendar.JANUARY; month--){
                    mMonthYears.add(new MonthYear(month, year));
                }
            }
        }

        mMonthlyAdapter = new MonthlyAdapter(requireActivity(), mMonthYears);
        mMonthViewPager.setAdapter(mMonthlyAdapter);
        selectCurMonth();
        return v;
    }

    @Override
    public void onDestroyView() {
        if (mMonthChangeCallback != null && mMonthViewPager != null){
            mMonthViewPager.unregisterOnPageChangeCallback(mMonthChangeCallback);
        }
        mMonthViewPager = null;
        super.onDestroyView();
    }

    private void selectCurMonth(){
        for (int i = 0; i < mMonthYears.size(); i++){
            MonthYear monthYear = mMonthYears.get(i);
            if (mMonthYear.getMonth() == monthYear.getMonth() && mMonthYear.getYear() == monthYear.getYear()){
                mMonthViewPager.setCurrentItem(i, false);
            }
        }
    }

    class MonthChangeCallback extends ViewPager2.OnPageChangeCallback{
        @Override
        public void onPageSelected(int position) {
            MonthYear monthYear = mMonthYears.get(position);
            String monthString = FormatUtils.getMonthName(requireContext(), monthYear.getMonth());
            String yearString = String.valueOf(monthYear.getYear());
            mMonthYearButton.setText(monthString + " " + yearString);
            AppPreferences.setPrefMonthYear(requireContext(), monthYear.toString());
            super.onPageSelected(position);
        }
    }
}
