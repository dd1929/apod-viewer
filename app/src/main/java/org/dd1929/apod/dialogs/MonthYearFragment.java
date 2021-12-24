package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.R;
import org.dd1929.apod.models.MonthYear;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.utils.FormatUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MonthYearFragment extends DialogFragment {

    private static final String ARG_CALLBACK = "callback";
    private static final String BUNDLE_MONTH = "curMonth";
    private static final String BUNDLE_YEAR = "curYear";

    private Spinner mMonthsSpinner, mYearsSpinner;
    private ArrayAdapter<String> mMonthsAdapter;
    private ArrayAdapter<Integer> mYearsAdapter;

    private int mCurMonth, mCurYear;
    private List<String> mMonthsList = new ArrayList<>();
    private List<Integer> mYearsList = new ArrayList<>();
    private MonthSelectionCallback mCallback;

    public static MonthYearFragment newInstance(MonthSelectionCallback callback) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALLBACK, callback);

        MonthYearFragment fragment = new MonthYearFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface MonthSelectionCallback extends Serializable {
        void onMonthSelected(MonthYear monthYear);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year, null);

        mCallback = (MonthSelectionCallback) getArguments().getSerializable(ARG_CALLBACK);

        if (savedInstanceState != null){
            mCurMonth = savedInstanceState.getInt(BUNDLE_MONTH);
            mCurYear = savedInstanceState.getInt(BUNDLE_YEAR);
        }

        if (mCurMonth == 0 || mCurYear == 0){
            String curMonthYear = AppPreferences.getPrefMonthYear(requireContext().getApplicationContext());
            if (curMonthYear == null){
                curMonthYear = Calendar.getInstance().get(Calendar.MONTH)
                        + " "
                        + Calendar.getInstance().get(Calendar.YEAR);
            }

            String[] monthYear = curMonthYear.split(" ");
            mCurMonth = Integer.parseInt(monthYear[0]);
            mCurYear = Integer.parseInt(monthYear[1]);
        }

        mMonthsSpinner = v.findViewById(R.id.months_spinner);
        mYearsSpinner = v.findViewById(R.id.years_spinner);
        mMonthsAdapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, mMonthsList);
        mYearsAdapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, mYearsList);
        mMonthsSpinner.setAdapter(mMonthsAdapter);
        mYearsSpinner.setAdapter(mYearsAdapter);

        int latestYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1995; i <= latestYear; i++){
            mYearsList.add(i);
        }
        mYearsAdapter.notifyDataSetChanged();

        mYearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               mMonthsList.clear();
                mCurYear = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
                if (mCurYear == 1995){
                    for (int month = Calendar.JUNE; month <= Calendar.DECEMBER; month++){
                        mMonthsList.add(FormatUtils.getMonthName(requireContext(), month));
                    }
                    mMonthsAdapter.notifyDataSetChanged();
                    if (!(mMonthsList.contains(FormatUtils.getMonthName(requireContext(), mCurMonth)))){
                        mMonthsSpinner.setSelection(0, true);
                    } else {
                        for (int m = 0; m < mMonthsList.size(); m++){
                            if (mMonthsList.get(m).equals(FormatUtils.getMonthName(requireContext(), mCurMonth))){
                                mMonthsSpinner.setSelection(m, true);
                            }
                        }
                    }
                } else if (mCurYear == Calendar.getInstance().get(Calendar.YEAR)) {
                    for (int month = Calendar.JANUARY; month <= Calendar.getInstance().get(Calendar.MONTH); month++){
                        mMonthsList.add(FormatUtils.getMonthName(requireContext(), month));
                    }
                    mMonthsAdapter.notifyDataSetChanged();
                    if (!(mMonthsList.contains(FormatUtils.getMonthName(requireContext(), mCurMonth)))){
                        mMonthsSpinner.setSelection(mMonthsList.size() - 1, true);
                    } else {
                        for (int m = 0; m < mMonthsList.size(); m++){
                            if (mMonthsList.get(m).equals(FormatUtils.getMonthName(requireContext(), mCurMonth))){
                                mMonthsSpinner.setSelection(m, true);
                            }
                        }
                    }
                } else {
                    for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++){
                        mMonthsList.add(FormatUtils.getMonthName(requireContext(), month));
                    }
                    mMonthsAdapter.notifyDataSetChanged();
                    for (int m = 0; m < mMonthsList.size(); m++){
                        if (mMonthsList.get(m).equals(FormatUtils.getMonthName(requireContext(), mCurMonth))){
                            mMonthsSpinner.setSelection(m, true);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMonthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurMonth = FormatUtils.getMonthInt(requireContext(), adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        for (int y = 0; y < mYearsList.size(); y++){
            if (mYearsList.get(y) == mCurYear){
                mYearsSpinner.setSelection(y, true);
            }
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(v)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    AppPreferences.setPrefMonthYear(requireContext(), mCurMonth + " " + mCurYear);
                    mCallback.onMonthSelected(new MonthYear(mCurMonth, mCurYear));
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_MONTH, mCurMonth);
        outState.putInt(BUNDLE_YEAR, mCurYear);
    }
}
