package org.dd1929.apod.database;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.dd1929.apod.models.APOD;
import org.dd1929.apod.R;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.utils.DateParseUtils;
import org.dd1929.apod.utils.APODQueryUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class APODRepository {

    private static final String TAG = "APODRepository";
    private static final String API_KEY = "DEMO_KEY";

    private APODQueryUtils mAPODQueryUtils;
    private APODQueryUtils.DataFetchListener mDataFetchListener;

    private APODDao mAPODDao;
    private LiveData<APOD> mAPOD;
    private LiveData<APOD> mLatestAPOD;
    private LiveData<List<APOD>> mAllAPODs;
    private LiveData<List<APOD>> mFavAPODs;
    private LiveData<List<APOD>> mRandomAPODs;

    public APODRepository(Context applicationContext){
        APODRoomDatabase db = APODRoomDatabase.getDatabase(applicationContext);

        mDataFetchListener = new APODQueryUtils.DataFetchListener() {
            @Override
            public void onDataFetched(APOD apod) {
                insert(apod, applicationContext);
            }

            @Override
            public void onDataFetched(List<APOD> apods) {
                if (apods.get(0).getIsRandom()){
                    apods.get(0).setIsRandom(false);
                    insertRandom(apods, applicationContext);
                } else {
                    insertList(apods, applicationContext);
                }
            }
        };

        String apiKey = API_KEY;
        if (AppPreferences.getPrefApiKey(applicationContext) != null
                && !AppPreferences.getPrefApiKey(applicationContext).trim().isEmpty()){
            apiKey = AppPreferences.getPrefApiKey(applicationContext);
        }
        mAPODQueryUtils = new APODQueryUtils(apiKey, applicationContext.getString(R.string.base_url), mDataFetchListener);

        mAPODDao = db.apodDao();
        mLatestAPOD = mAPODDao.getLatestAPOD();
        mAllAPODs = mAPODDao.getAllAPODs();
        mFavAPODs = mAPODDao.getFavAPODs();
        mRandomAPODs = mAPODDao.getRandomAPODs();

    }

    LiveData<APOD> getAPOD(Date date){
        mAPODQueryUtils.fetchSingle(date);
        mAPOD = mAPODDao.getAPOD(date.getTime());
        return mAPOD;
    }

    LiveData<APOD> getLatestAPOD(){
        mAPODQueryUtils.fetchSingle(null);
        return mLatestAPOD;
    }

    LiveData<List<APOD>> getAllAPODs(){
        return mAllAPODs;
    }

    LiveData<List<APOD>> getFavAPODs(){
        return mFavAPODs;
    }

    LiveData<List<APOD>> getRandomAPODs(){
        return mRandomAPODs;
    }

    LiveData<List<APOD>> getRangeAPODs(Date startDate, Date endDate){

        Date mStartDate, mEndDate = null;
        Calendar curCal = Calendar.getInstance(); //Calendar object with current time
        Calendar startCal = Calendar.getInstance(), endCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal.setTime(endDate);
        if ((startCal.get(Calendar.MONTH) == Calendar.JUNE)
                && (startCal.get(Calendar.YEAR) == 1995)){
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(1995, Calendar.JUNE, 16);
            mStartDate = startCalendar.getTime();
            mEndDate = endDate;
        } else if ((endCal.get(Calendar.MONTH) == curCal.get(Calendar.MONTH))
                && (endCal.get(Calendar.YEAR) == curCal.get(Calendar.YEAR))){
            mStartDate = startDate;
        } else {
            mStartDate = startDate;
            mEndDate = endDate;
        }

        refreshRange(mStartDate, mEndDate);
        return mAPODDao.getRangeAPODs(startDate.getTime(), endDate.getTime());
    }

    public void insert(APOD apod, Context context){
        checkAndUpdateIfLatest(apod, context);
        APODRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (apod != null){
                mAPODDao.insert(apod);
            }
        });
    }

    void insertList(List<APOD> apods, Context context){
        Collections.sort(apods, (a1, a2) -> -a1.getDate().compareTo(a2.getDate()));
        APOD apod = apods.get(0);
        checkAndUpdateIfLatest(apod, context);
        APODRoomDatabase.databaseWriteExecutor.execute(() -> mAPODDao.insert(apods));
    }

    void insertRandom(List<APOD> apods, Context context){
        Collections.sort(apods, (a1, a2) -> -a1.getDate().compareTo(a2.getDate()));
        APOD apod = apods.get(0);
        checkAndUpdateIfLatest(apod, context);
        APODRoomDatabase.databaseWriteExecutor.execute(() -> {
            mAPODDao.removeRandomAPODs();
            mAPODDao.insert(apods);

            for (APOD apod1 : apods){
                mAPODDao.makeRandom(apod1.getDate(), true);
            }
        });
    }

    void deleteAll(){
        APODRoomDatabase.databaseWriteExecutor.execute(() -> {
            mAPODDao.deleteAll();
        });
    }

    void makeFav(long date, boolean isFav){
        APODRoomDatabase.databaseWriteExecutor.execute(() -> mAPODDao.makeFavourite(date, isFav));
    }

    void refreshRandom(int count){
        mAPODQueryUtils.fetchRandom(count);
    }

    void refreshRange(Date startDate, Date endDate){
        String mStartDate = null, mEndDate = null;
        if (startDate != null){
            mStartDate = DateParseUtils.getStringFromDate(startDate);
        }
        if (endDate != null){
            mEndDate = DateParseUtils.getStringFromDate(endDate);
        }
        mAPODQueryUtils.fetchRange(mStartDate, mEndDate);
    }

    public static void checkAndUpdateIfLatest(APOD apod, Context context){
        if (apod != null){
            if (apod.getDate().compareTo(AppPreferences.getLatestAPODDate(context)) > 0){
                AppPreferences.setLatestApodDate(context, apod.getDate());

                if (AppPreferences.getPrefSetAutoWallpaper(context) && apod.getMediaType().equals("image")){
                    String url = apod.getUrl();
                    if (AppPreferences.getPrefQualityAutoWallpaper(context).equals("high") && apod.getHdUrl() != null){
                        url = apod.getHdUrl();
                    }
                    Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

                            String wallpaperTarget = AppPreferences.getPrefWallpaperTarget(context);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                if (wallpaperTarget.equals("home")){
                                    try {
                                        wallpaperManager.setBitmap(resource, null, false, WallpaperManager.FLAG_SYSTEM);
                                    } catch (IOException e) {
                                        Log.e(TAG, "Failed to set wallpaper");
                                    }
                                } else if (wallpaperTarget.equals("lock")){
                                    try {
                                        wallpaperManager.setBitmap(resource, null, false, WallpaperManager.FLAG_LOCK);
                                    } catch (IOException e) {
                                        Log.e(TAG, "Failed to set wallpaper");
                                    }
                                } else {
                                    try {
                                        wallpaperManager.setBitmap(resource, null, false, WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                                    } catch (IOException e) {
                                        Log.e(TAG, "Failed to set wallpaper");
                                    }
                                }
                            } else {
                                try {
                                    wallpaperManager.setBitmap(resource);
                                } catch (IOException e) {
                                    Log.e(TAG, "Failed to set wallpaper");
                                }
                            }

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            }
        }
    }
}
