package org.dd1929.apod.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.dd1929.apod.models.APOD;

import java.util.Date;
import java.util.List;

public class APODViewModel extends AndroidViewModel {

    private static final String TAG = "APODViewModel";

    private APODRepository mRepository;

    private LiveData<APOD> mAPOD;
    private final LiveData<APOD> mLatestAPOD;
    private final LiveData<List<APOD>> mAllAPODs;
    private final LiveData<List<APOD>> mFavAPODs;

    public APODViewModel (Application application){
        super(application);
        mRepository = new APODRepository(application);

        mLatestAPOD = mRepository.getLatestAPOD();
        mAllAPODs = mRepository.getAllAPODs();
        mFavAPODs = mRepository.getFavAPODs();
    }

    public LiveData<APOD> getLatestAPOD(){
        return mLatestAPOD;
    }

    public LiveData<List<APOD>> getAllAPODs(){
        return mAllAPODs;
    }

    public LiveData<List<APOD>> getFavAPODs(){
        return mFavAPODs;
    }

    public LiveData<List<APOD>> getRandomAPODs() {
        return mRepository.getRandomAPODs();
    }

    public LiveData<List<APOD>> getRangeAPODs(Date startDate, Date endDate){
        Log.i(TAG, "Got Range Request: " + startDate + endDate);
        return mRepository.getRangeAPODs(startDate, endDate);
    }

    public void insert(APOD apod){
        mRepository.insert(apod, getApplication());
    }

    public void deleteAll(){
        mRepository.deleteAll();
    }

    public void refreshRandomAPODs(int count){
        mRepository.refreshRandom(count);
    }

    public void makeFav(Long date, boolean isFav){
        mRepository.makeFav(date, isFav);
    }

    public LiveData<APOD> getAPOD(Date date){
        mAPOD = mRepository.getAPOD(date);
        return mAPOD;
    }
}
