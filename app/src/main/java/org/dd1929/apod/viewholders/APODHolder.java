package org.dd1929.apod.viewholders;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.dd1929.apod.activities.DetailsActivity;
import org.dd1929.apod.fragments.DiscoverFragment;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.R;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.utils.TextDrawable;

import java.util.Date;

public class APODHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private APOD mAPOD;
    private ImageView mAPODImageView;
    private ProgressBar mProgressBar;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private ImageButton mFavButton;
    private ImageButton mMenuButton;

    private Callbacks mCallbacks;

    public interface Callbacks{
        void onFavChanged(long date, boolean isFav);
        void onActionInvoked(APOD apod, String action);
    }

    public void setCallbacks(Callbacks callbacks){
        mCallbacks = callbacks;
    }

    public static APODHolder create(ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (AppPreferences.getPrefView(parent.getContext()).equals("list")){
            itemView = inflater.inflate(R.layout.list_item_list_view, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.list_item_grid_view, parent, false);
        }
        return new APODHolder(itemView);
    }

    public APODHolder(View itemView){
        super(itemView);

        mAPODImageView = itemView.findViewById(R.id.apod_image_view);
        mProgressBar = itemView.findViewById(R.id.progress_bar);
        mTitleTextView = itemView.findViewById(R.id.title_text_view);
        mDateTextView = itemView.findViewById(R.id.date_text_view);
        mFavButton = itemView.findViewById(R.id.favourite_button);
        mMenuButton = itemView.findViewById(R.id.menu_button);

        mFavButton.setOnClickListener(view -> mCallbacks.onFavChanged(mAPOD.getDate(), !mAPOD.getIsFavourite()));

        mMenuButton.setOnClickListener(view -> showPopupMenu());

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    private void bindFav(){
        if (mAPOD.getIsFavourite()){
            mFavButton.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_bookmarked));
        } else {
            mFavButton.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_not_bookmarked));
        }
    }

    private void bindText(){
        mTitleTextView.setText(mAPOD.getTitle());
        mDateTextView.setText(DateFormat.getDateFormat(itemView.getContext()).format(new Date(mAPOD.getDate())));
        mAPODImageView.setContentDescription(mAPOD.getTitle());
    }

    private void bindImage(){
        if (mAPOD.getMediaType() != null){
            if (mAPOD.getMediaType().equals("image")){
                mProgressBar.setVisibility(View.VISIBLE);
                Glide.with(itemView)
                        .load(mAPOD.getUrl())
                        .fallback(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mAPODImageView);
            } else if (mAPOD.getMediaType().equals("video")){
                mProgressBar.setVisibility(View.VISIBLE);
                Glide.with(itemView)
                        .load(mAPOD.getThumbUrl())
                        .fallback(R.drawable.ic_video_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mAPODImageView);
            }
        }
    }

    public void bindAPOD(APOD apod){
        mAPOD = apod;
        bindImage();
        bindText();
        bindFav();
    }

    @Override
    public void onClick(View view) {
        Intent detailsIntent = DetailsActivity.newIntent(view.getContext(), new Date(mAPOD.getDate()), false, false);
        view.getContext().startActivity(detailsIntent);
    }

    @Override
    public boolean onLongClick(View view) {
        showPopupMenu();
        return true;
    }

    private void showPopupMenu(){
        PopupMenu menu = new PopupMenu(itemView.getContext(), mMenuButton);

        menu.getMenu().add(itemView.getContext().getString(R.string.open_in_browser));
        menu.getMenu().add(itemView.getContext().getString(R.string.share));
        if (mAPOD.getMediaType().equals("image")){
            menu.getMenu().add(itemView.getContext().getString(R.string.set_as));
            menu.getMenu().add(itemView.getContext().getString(R.string.save));
        }

        menu.setOnMenuItemClickListener(item -> { //PopupMenu.OnMenuItemClickListener
            CharSequence title = item.getTitle();
            if (itemView.getContext().getString(R.string.open_in_browser).equals(title)) {
                mCallbacks.onActionInvoked(mAPOD, DiscoverFragment.ACTION_OPEN_BROWSER);
            } else if (itemView.getContext().getString(R.string.set_as).equals(title)) {
                mCallbacks.onActionInvoked(mAPOD, DiscoverFragment.ACTION_SET_AS);
            } else if (itemView.getContext().getString(R.string.save).equals(title)) {
                mCallbacks.onActionInvoked(mAPOD, DiscoverFragment.ACTION_SAVE);
            } else if (itemView.getContext().getString(R.string.share).equals(title)) {
                mCallbacks.onActionInvoked(mAPOD, DiscoverFragment.ACTION_SHARE);
            }
            return true;
        });

        menu.show();
    }
}
