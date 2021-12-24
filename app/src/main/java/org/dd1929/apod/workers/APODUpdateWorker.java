package org.dd1929.apod.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.dd1929.apod.activities.DetailsActivity;
import org.dd1929.apod.database.APODRepository;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;
import org.dd1929.apod.utils.APODQueryUtils;
import org.dd1929.apod.utils.DateParseUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class APODUpdateWorker extends Worker {

    private static final String TAG = "APODUpdateWorker";
    private static final String API_KEY = "DEMO_KEY";
    private static final String CHANNEL_ID = "New APODs";

    private APODRepository mRepository;
    private APODQueryUtils mAPODQueryUtils;
    private APODQueryUtils.DataFetchListener mDataFetchListener;

    public APODUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        mRepository = new APODRepository(getApplicationContext());
        mDataFetchListener = new APODQueryUtils.DataFetchListener() {
            @Override
            public void onDataFetched(APOD apod) {
                boolean isLatest = apod.getDate().compareTo(AppPreferences.getLatestAPODDate(getApplicationContext())) > 0;
                mRepository.insert(apod, getApplicationContext());
                Log.i(TAG, "Checked for APOD");
                if (isLatest && AppPreferences.getPrefNotifications(getApplicationContext())){
                    Log.i(TAG, "New APOD");
                    if (apod.getMediaType().equals("image")){
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(apod.getUrl())
                                .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Resources res = getApplicationContext().getResources();

                                Intent viewIntent = DetailsActivity.newIntent(getApplicationContext(), new Date(apod.getDate()), false, false);
                                Intent shareIntent = DetailsActivity.newIntent(getApplicationContext(), new Date(apod.getDate()), true, false);
                                Intent setAsIntent = DetailsActivity.newIntent(getApplicationContext(), new Date(apod.getDate()), false, true);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(DateParseUtils.getUrlFromDate(new Date(apod.getDate())));
                                PendingIntent viewPi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                PendingIntent sharePi = PendingIntent.getActivity(getApplicationContext(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                PendingIntent setAsPi = PendingIntent.getActivity(getApplicationContext(), 0, setAsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                PendingIntent browserPi = PendingIntent.getActivity(getApplicationContext(), 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setTicker(res.getString(R.string.new_apod))
                                        .setSmallIcon(R.drawable.ic_saturn)
                                        .setContentTitle(apod.getTitle())
                                        .setContentText(apod.getDetails())
                                        .setContentIntent(viewPi)
                                        .setAutoCancel(true)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setLargeIcon(resource)
                                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource))
                                        .addAction(R.drawable.ic_settings_share, res.getString(R.string.share), sharePi)
                                        .addAction(R.drawable.ic_settings_set_as, res.getString(R.string.set_as), setAsPi)
                                        .addAction(R.drawable.ic_settings_world, res.getString(R.string.open_in_browser), browserPi);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, res.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                    notification.setChannelId(CHANNEL_ID);
                                }

                                notificationManager.notify(0, notification.build());
                            }

                            @Override
                            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                            }
                        });
                    } else {
                        Resources res = getApplicationContext().getResources();
                        Intent viewIntent = DetailsActivity.newIntent(getApplicationContext(), new Date(apod.getDate()), false, false);
                        Intent shareIntent = DetailsActivity.newIntent(getApplicationContext(), new Date(apod.getDate()), true, false);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(DateParseUtils.getUrlFromDate(new Date(apod.getDate())));
                        PendingIntent viewPi = PendingIntent.getActivity(getApplicationContext(), 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent sharePi = PendingIntent.getActivity(getApplicationContext(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent browserPi = PendingIntent.getActivity(getApplicationContext(), 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setTicker(res.getString(R.string.new_apod))
                                .setSmallIcon(R.drawable.ic_saturn)
                                .setContentTitle(apod.getTitle())
                                .setContentText(apod.getDetails())
                                .setContentIntent(viewPi)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .addAction(R.drawable.ic_settings_share, res.getString(R.string.share), sharePi)
                                .addAction(R.drawable.ic_settings_world, res.getString(R.string.open_in_browser), browserPi);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, res.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                            notification.setChannelId(CHANNEL_ID);
                        }

                        notificationManager.notify(0, notification.build());
                    }
                } else {
                    Log.i(TAG, "Old APOD");
                }
            }

            @Override
            public void onDataFetched(List<APOD> apods) {}
        };

        String apiKey = API_KEY;
        if (AppPreferences.getPrefApiKey(getApplicationContext()) != null
                && !AppPreferences.getPrefApiKey(getApplicationContext()).trim().isEmpty()){
            apiKey = AppPreferences.getPrefApiKey(getApplicationContext());
        }
        mAPODQueryUtils = new APODQueryUtils(apiKey, getApplicationContext().getString(R.string.base_url), mDataFetchListener);
        mAPODQueryUtils.fetchSingle(null);
        return Result.success();
    }
}
