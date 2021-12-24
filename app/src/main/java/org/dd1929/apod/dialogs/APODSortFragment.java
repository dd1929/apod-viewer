package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;

public class APODSortFragment extends DialogFragment {

    private RadioGroup mSortByRadioGroup, mOrderRadioGroup;
    private RadioButton mTitleButton, mDateButton, mCopyrightButton, mDetailsButton;
    private RadioButton mAscButton, mDescButton;

    public static APODSortFragment newInstance() {
        return new APODSortFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sort_apods, null);

        mSortByRadioGroup = v.findViewById(R.id.sort_by_radio_group);
        mOrderRadioGroup = v.findViewById(R.id.sort_order_radio_group);

        mTitleButton = v.findViewById(R.id.title_attr);
        mDateButton = v.findViewById(R.id.date_attr);
        mCopyrightButton = v.findViewById(R.id.copyright_attr);
        mDetailsButton = v.findViewById(R.id.details_attr);
        mAscButton = v.findViewById(R.id.ascending);
        mDescButton = v.findViewById(R.id.descending);

        switch (AppPreferences.getPrefSortBy(requireContext())){
            case AppPreferences.TITLE:
                mTitleButton.setChecked(true);
                break;
            case AppPreferences.COPYRIGHT:
                mCopyrightButton.setChecked(true);
                break;
            case AppPreferences.DETAILS:
                mDetailsButton.setChecked(true);
                break;
            default:
                mDateButton.setChecked(true);
        }

        if (AppPreferences.getPrefSortOrder(requireContext()).equals(AppPreferences.ASC)){
            mAscButton.setChecked(true);
        } else {
            mDescButton.setChecked(true);
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(v)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    int sortByButtonId = mSortByRadioGroup.getCheckedRadioButtonId();
                    int sortOrderButtonId = mOrderRadioGroup.getCheckedRadioButtonId();
                    String sortBy, sortOrder;

                    if (sortByButtonId == R.id.title_attr){
                        sortBy = AppPreferences.TITLE;
                    } else if (sortByButtonId == R.id.copyright_attr){
                        sortBy = AppPreferences.COPYRIGHT;
                    } else if (sortByButtonId == R.id.details_attr){
                        sortBy = AppPreferences.DETAILS;
                    } else {
                        sortBy = AppPreferences.DATE;
                    }

                    if (sortOrderButtonId == R.id.ascending){
                        sortOrder = AppPreferences.ASC;
                    } else {
                        sortOrder = AppPreferences.DESC;
                    }

                    AppPreferences.setPrefSortBy(requireContext(), sortBy);
                    AppPreferences.setPrefSortOrder(requireContext(), sortOrder);
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
