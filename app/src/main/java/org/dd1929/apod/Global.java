package org.dd1929.apod;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.dd1929.apod.executors.FileOpsExecutor;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.workers.APODUpdateWorker;

import java.util.concurrent.TimeUnit;

public class Global extends Application {

    private static final String CHANNEL_ID = "New APODs";

    @Override
    public void onCreate() {
        if (!AppPreferences.getPrefTheme(getApplicationContext()).equals("system")){
            if (AppPreferences.getPrefTheme(getApplicationContext()).equals("dark")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (AppPreferences.getPrefTheme(getApplicationContext()).equals("light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        final PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(APODUpdateWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        if (!AppPreferences.getPrefBgUpdates(getApplicationContext()).equals(false)){
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        FileOpsExecutor.stop();
        super.onTerminate();
    }
}
