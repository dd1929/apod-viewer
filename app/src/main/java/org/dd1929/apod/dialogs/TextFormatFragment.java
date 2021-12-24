package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class TextFormatFragment extends DialogFragment {

    private static final String ARG_LISTENER = "listener";
    private static final int SEEK_BAR_OFFSET = 5;

    private SeekBar mSizeSeekbar;
    private EditText mSizeEditText;
    private TextView mSizeErrorTextView;
    private RadioGroup mTypeRadioGroup;
    private RadioButton mNormalTypeButton, mSansTypeButton, mSerifTypeButton, mMonoTypeButton;

    private OnTextFormatChangeListener mListener;

    public interface OnTextFormatChangeListener extends Serializable {
        void onTextFormatChanged();
    }

    public static TextFormatFragment newInstance(OnTextFormatChangeListener listener){
        Bundle args = new Bundle();
        args.putSerializable(ARG_LISTENER, listener);

        TextFormatFragment fragment = new TextFormatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_format, null);

        mListener = (OnTextFormatChangeListener) getArguments().getSerializable(ARG_LISTENER);

        mSizeSeekbar = v.findViewById(R.id.size_seek_bar);
        mSizeEditText = v.findViewById(R.id.size_edit_text);
        mSizeErrorTextView = v.findViewById(R.id.size_error_text_view);
        mTypeRadioGroup = v.findViewById(R.id.type_radio_group);

        mNormalTypeButton = v.findViewById(R.id.normal_type);
        mSansTypeButton = v.findViewById(R.id.sans_type);
        mSerifTypeButton = v.findViewById(R.id.serif_type);
        mMonoTypeButton = v.findViewById(R.id.mono_type);

        mSizeSeekbar.setProgress(AppPreferences.getPrefFontSize(getContext()) - SEEK_BAR_OFFSET);
        mSizeEditText.setText(String.valueOf(AppPreferences.getPrefFontSize(getContext())));

        switch (AppPreferences.getPrefFontType(getContext())){
            case "normal":
                mNormalTypeButton.setChecked(true);
                break;
            case "sans":
                mSansTypeButton.setChecked(true);
                break;
            case "serif":
                mSerifTypeButton.setChecked(true);
                break;
            case "mono":
                mMonoTypeButton.setChecked(true);
                break;
        }

        mSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                AppPreferences.setPrefFontSize(getContext(), i + SEEK_BAR_OFFSET);
                mSizeEditText.setText(String.valueOf(i + SEEK_BAR_OFFSET));
                mListener.onTextFormatChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSizeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int size;
                if (!mSizeEditText.getText().toString().equals("")){
                    size = Integer.parseInt(mSizeEditText.getText().toString());
                } else {
                    size = 0;
                }

                if (size < 5){
                    mSizeErrorTextView.setVisibility(View.VISIBLE);
                } else if (size > 40){
                    mSizeErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    mSizeErrorTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSizeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int size;
                    if (!mSizeEditText.getText().toString().equals("")){
                        size = Integer.parseInt(mSizeEditText.getText().toString());
                    } else {
                        size = 0;
                    }

                    if (size < 5){
                        mSizeSeekbar.setProgress(5 - SEEK_BAR_OFFSET);
                    } else if (size > 40){
                        mSizeSeekbar.setProgress(40 - SEEK_BAR_OFFSET);
                    } else {
                        mSizeSeekbar.setProgress(size - SEEK_BAR_OFFSET);
                    }
                    return true;
                }
                return false;
            }
        });

        mTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.normal_type){
                    AppPreferences.setPrefFontType(getContext(), "normal");
                } else if (i == R.id.sans_type){
                    AppPreferences.setPrefFontType(getContext(), "sans");
                } else if (i == R.id.serif_type){
                    AppPreferences.setPrefFontType(getContext(), "serif");
                } else if (i == R.id.mono_type){
                    AppPreferences.setPrefFontType(getContext(), "mono");
                }

                mListener.onTextFormatChanged();
            }
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(v)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
