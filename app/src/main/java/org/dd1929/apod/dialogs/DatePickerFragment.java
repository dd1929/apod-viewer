package org.dd1929.apod.dialogs;

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
    along with this program.  If not, see <https://www.gnu.org/licenses/ */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.R;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_LISTENER = "listener";

    private DatePicker mDatePicker;
    private OnDateSelectedListener mListener;

    public interface OnDateSelectedListener extends Serializable {
        void onDateSelected(Date date);
    }

    public static DatePickerFragment newInstance(Date date, OnDateSelectedListener listener){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_LISTENER, listener);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date, null);

        mListener = (OnDateSelectedListener) getArguments().getSerializable(ARG_LISTENER);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.setMaxDate(System.currentTimeMillis());
        mDatePicker.setMinDate(new GregorianCalendar(1995, 05, 16).getTimeInMillis());
        mDatePicker.init(year, month, day, null);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.date_picker_title)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        if (date.getTime() < mDatePicker.getMinDate()){
                            date = new Date(mDatePicker.getMinDate());
                        } else if (date.getTime() > mDatePicker.getMaxDate()){
                            date = new Date(mDatePicker.getMaxDate());
                        }
                        mListener.onDateSelected(date);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dismiss()) //DialogInterface.OnClickListener
                .create();
    }
}
