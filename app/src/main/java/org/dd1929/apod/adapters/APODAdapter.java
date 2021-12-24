package org.dd1929.apod.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.dd1929.apod.viewholders.APODHolder;
import org.dd1929.apod.models.APOD;

import java.util.List;

public class APODAdapter extends RecyclerView.Adapter<APODHolder> {

    private APODHolder.Callbacks mCallbacks;
    private List<APOD> mAPODs;

    public APODAdapter (APODHolder.Callbacks callbacks){
        mCallbacks = callbacks;
    }

    @NonNull
    @Override
    public APODHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        APODHolder holder = APODHolder.create(parent);
        holder.setCallbacks(mCallbacks);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull APODHolder holder, int position) {
        APOD apod = mAPODs.get(position);
        holder.bindAPOD(apod);
    }

    @Override
    public int getItemCount() {
        if (mAPODs != null){
            return mAPODs.size();
        } else {
            return 0;
        }
    }

    public void setAPODs(List<APOD> apods){
        mAPODs = apods;
    }
}
