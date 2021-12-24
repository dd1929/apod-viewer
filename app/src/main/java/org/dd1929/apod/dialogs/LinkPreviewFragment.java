package org.dd1929.apod.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.dd1929.apod.R;
import org.dd1929.apod.utils.IntentUtils;
import org.jetbrains.annotations.NotNull;

public class LinkPreviewFragment extends DialogFragment {

    private static final String ARG_LINK = "link";

    private TextView mLinkTextView;

    public static LinkPreviewFragment newInstance(String link) {
        Bundle args = new Bundle();
        args.putString(ARG_LINK, link);

        LinkPreviewFragment fragment = new LinkPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_display, null);

        String link = getArguments().getString(ARG_LINK);
        mLinkTextView = v.findViewById(R.id.text_view);
        mLinkTextView.setText(getString(R.string.link, link));

        return new MaterialAlertDialogBuilder(getContext())
                .setView(v)
                .setTitle(R.string.link_preview)
                .setPositiveButton(R.string.open_in_browser, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IntentUtils.sendWebIntent(Uri.parse(link), getContext());
                    }
                })
                .create();
    }
}
