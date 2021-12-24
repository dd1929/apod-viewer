package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.R;
import org.jetbrains.annotations.NotNull;

public class ImageQualityFragment extends DialogFragment {

    private RadioGroup mQualityRadioGroup;
    private CheckBox mSavePrefCheckBox;

    protected String mQuality;
    private OnChooseListener mListener;
    private OnQualityChangedListener mQualityListener;

    public interface OnChooseListener{
        void onOptionChosen(String quality, boolean savePrefs);
    }

    public interface OnQualityChangedListener{
        void onQualityChanged(String quality);
    }

    public static ImageQualityFragment newInstance(OnChooseListener listener, String curQuality){
        return new ImageQualityFragment(listener, curQuality);
    }

    protected ImageQualityFragment(OnChooseListener listener, String curQuality){
        mQuality = curQuality;
        mListener = listener;
        mQualityListener = quality -> mQuality = quality;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image_quality, null);

        mQualityRadioGroup = v.findViewById(R.id.quality_radio_group);
        mSavePrefCheckBox = v.findViewById(R.id.save_pref_check_box);

        mQualityRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.low:
                    mQualityListener.onQualityChanged("low");
                    break;
                case R.id.high:
                    mQualityListener.onQualityChanged("high");
                    break;
            }
        });

        return new MaterialAlertDialogBuilder(getContext())
                .setView(v)
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> { //DialogInterface.OnClickListener
                    mListener.onOptionChosen(mQuality, mSavePrefCheckBox.isChecked());
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
