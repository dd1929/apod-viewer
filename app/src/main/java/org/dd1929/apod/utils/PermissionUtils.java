package org.dd1929.apod.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean checkReadStoragePermission(Context context){
        boolean hasReadPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        return hasReadPermission;
    }

    public static boolean checkWriteStoragePermission(Context context){
        boolean hasWritePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        return hasWritePermission || minSdk29;
    }
}
