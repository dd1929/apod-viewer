package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.R;
import org.jetbrains.annotations.NotNull;

public class GenericMessageFragment extends DialogFragment {

    private static final String ARG_TEXT = "textToDisplay";

    private OnChooseOkListener mListener;

    public interface OnChooseOkListener{
        void onOkChosen();
    }

    public static GenericMessageFragment newInstance(String text, OnChooseOkListener listener){
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);

        GenericMessageFragment fragment = new GenericMessageFragment(listener);
        fragment.setArguments(args);

        return fragment;
    }

    private GenericMessageFragment(OnChooseOkListener listener){
        mListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getContext())
                .setMessage(requireArguments().getString(ARG_TEXT))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                        mListener.onOkChosen();
                    }
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
