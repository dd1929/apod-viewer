package org.dd1929.apod.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import org.dd1929.apod.models.Library;
import org.dd1929.apod.viewholders.LibsHolder;
import org.jetbrains.annotations.NotNull;

public class LibsAdapter extends ListAdapter<Library, LibsHolder> {

    private LibsHolder.OnLicenseClickListener mListener;

    public LibsAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Library> diffCallback, LibsHolder.OnLicenseClickListener listener) {
        super(diffCallback);
        mListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public LibsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LibsHolder holder = LibsHolder.create(parent);
        holder.setLicenseClickListener(mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LibsHolder holder, int position) {
        Library library = getItem(position);
        holder.bindLibrary(library);
    }

    public static class LibsDiff extends DiffUtil.ItemCallback<Library>{

        @Override
        public boolean areItemsTheSame(@NonNull Library oldItem, @NonNull Library newItem){
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Library oldItem, @NonNull Library newItem){
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
