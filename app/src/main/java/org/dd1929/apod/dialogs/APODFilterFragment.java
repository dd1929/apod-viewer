package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;

public class APODFilterFragment extends DialogFragment {

    private MaterialCheckBox mImageCheckBox, mVideoCheckBox;
    private MaterialCheckBox mWithCopyrightCheckBox, mWithoutCopyrightCheckBox;

    public static APODFilterFragment newInstance() {
        return new APODFilterFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter_apods, null);

        mImageCheckBox = v.findViewById(R.id.image_media);
        mVideoCheckBox = v.findViewById(R.id.video_media);
        mWithCopyrightCheckBox = v.findViewById(R.id.with_copyright);
        mWithoutCopyrightCheckBox = v.findViewById(R.id.without_copyright);

        String mediaTypeFilter = AppPreferences.getPrefFilterMediaType(requireContext());
        String copyrightFilter = AppPreferences.getPrefFilterCopyright(requireContext());

        if (mediaTypeFilter.contains(AppPreferences.IMAGE)){
            mImageCheckBox.setChecked(true);
        }
        if (mediaTypeFilter.contains(AppPreferences.VIDEO)){
            mVideoCheckBox.setChecked(true);
        }

        if (copyrightFilter.contains(AppPreferences.WITH_CR)){
            mWithCopyrightCheckBox.setChecked(true);
        }
        if (copyrightFilter.contains(AppPreferences.WITHOUT_CR)){
            mWithoutCopyrightCheckBox.setChecked(true);
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mediaTypeFilter = "";
                        String copyrightFilter = "";

                        if (mImageCheckBox.isChecked()){
                            mediaTypeFilter += (", " + AppPreferences.IMAGE);
                        }
                        if (mVideoCheckBox.isChecked()){
                            mediaTypeFilter += (", " + AppPreferences.VIDEO);
                        }

                        if (mWithCopyrightCheckBox.isChecked()){
                            copyrightFilter += (", " + AppPreferences.WITH_CR);
                        }
                        if (mWithoutCopyrightCheckBox.isChecked()){
                            copyrightFilter += (", " + AppPreferences.WITHOUT_CR);
                        }

                        AppPreferences.setPrefFilterMediaType(requireContext(), mediaTypeFilter);
                        AppPreferences.setPrefFilterCopyright(requireContext(), copyrightFilter);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }
}
