package org.dd1929.apod.viewholders;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.dd1929.apod.models.Library;
import org.dd1929.apod.R;

public class LibsHolder extends RecyclerView.ViewHolder{

    private TextView mTitleTextView, mAuthorTextView;
    private Button mLicenseButton, mWebsiteButton, mSourceButton;

    private OnLicenseClickListener mListener;

    public interface OnLicenseClickListener{
        void onLicenseClicked(Library lib);
    }

    public void setLicenseClickListener(OnLicenseClickListener listener){
        mListener = listener;
    }


    public static LibsHolder create(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_library, parent, false);
        return new LibsHolder(itemView);
    }

    public LibsHolder(@NonNull View itemView) {
        super(itemView);

        mTitleTextView = itemView.findViewById(R.id.title_text_view);
        mAuthorTextView = itemView.findViewById(R.id.author_text_view);
        mLicenseButton = itemView.findViewById(R.id.view_license_btn);
        mWebsiteButton = itemView.findViewById(R.id.website_btn);
        mSourceButton = itemView.findViewById(R.id.view_source_btn);
    }

    public void bindLibrary(Library library){
        mTitleTextView.setText(library.getName());
        mAuthorTextView.setText(library.getAuthor());

        mLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLicenseClicked(library);
            }
        });

        if (library.getWebsite() != null){
            mWebsiteButton.setVisibility(View.VISIBLE);
            mWebsiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendWebIntent(Uri.parse(library.getWebsite()), view);
                }
            });
        } else {
            mWebsiteButton.setVisibility(View.GONE);
        }

        mSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWebIntent(Uri.parse(library.getSourceUrl()), view);
            }
        });
    }

    private void sendWebIntent(Uri url, View view){
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(url);
        view.getContext().startActivity(webIntent);
    }
}
