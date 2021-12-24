package org.dd1929.apod.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import org.dd1929.apod.models.FileOpsSuccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StorageUtils {

    private static final String TAG = "StorageUtils";

    public static FileOpsSuccess saveFile(File resource, String filename, String mimeType, ContentResolver contentResolver){
        if (resource == null){
            return new FileOpsSuccess(false, null);
        }

        boolean isSuccessful;
        File savedFile = null;
        if (isExternalStorageWritable()){
            try {
                InputStream is = null;
                OutputStream os = null;
                ParcelFileDescriptor pfd = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "APODs");
                        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        savedFile = new File(imageUri.getPath());

                        pfd = contentResolver.openFileDescriptor(imageUri, "w");
                        os = new FileOutputStream(pfd.getFileDescriptor());
                    } else {
                        File saveDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "APODs");

                        if (!saveDir.exists()){
                            if (!saveDir.mkdir()){
                                return new FileOpsSuccess(false, null);
                            }
                        }

                        File saveFile = new File(saveDir, filename);
                        savedFile = saveFile;

                        os = new FileOutputStream(saveFile);
                    }

                    is = contentResolver.openInputStream(Uri.fromFile(resource));

                    byte[] buffer = new byte[2048];

                    long fileSize = resource.length();
                    long fileSizeSaved = 0;

                    while (true){
                        int read = is.read(buffer);

                        if (read == -1){
                            break;
                        }

                        os.write(buffer, 0, read);

                        fileSizeSaved += read;

                        Log.d(TAG, "Saved " + fileSizeSaved + " of " + fileSize);
                    }

                    isSuccessful = true;

                    os.flush();

                } catch (FileNotFoundException e) {
                    Log.d(TAG, e.getMessage());
                    isSuccessful = false;
                } finally {
                    if (is != null) {
                        is.close();
                    }

                    if (os != null) {
                        os.close();
                    }

                    if (pfd != null) {
                        pfd.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                isSuccessful = false;
            }
        } else {
            isSuccessful = false;
        }

        return new FileOpsSuccess(isSuccessful, savedFile);
    }

    public static FileOpsSuccess cacheFile(File resource, String dst, String filename, ContentResolver resolver){
        if (resource == null){
            return new FileOpsSuccess(false, null);
        }

        boolean isSuccessful;
        File savedFile = null;
        try {
            InputStream is = null;
            OutputStream os = null;
            try {
                File saveDir = new File(dst);

                if (!saveDir.exists()){
                    if (!saveDir.mkdir()){
                        return new FileOpsSuccess(false, null);
                    }
                }

                File saveFile = new File(saveDir.getAbsolutePath() + File.separator + filename);
                savedFile = saveFile;

                os = new FileOutputStream(saveFile);

                is = resolver.openInputStream(Uri.fromFile(resource));

                byte[] buffer = new byte[2048];

                long fileSize = resource.length();
                long fileSizeSaved = 0;

                while (true){
                    int read = is.read(buffer);

                    if (read == -1){
                        break;
                    }

                    os.write(buffer, 0, read);

                    fileSizeSaved += read;

                    Log.d(TAG, "Saved " + fileSizeSaved + " of " + fileSize);
                }

                isSuccessful = true;

                os.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                isSuccessful = false;
            } finally {
                if (is != null){
                    is.close();
                }

                if (os != null){
                    os.close();
                }
            }
        } catch (IOException ioe){
            Log.e(TAG, ioe.getLocalizedMessage());
            isSuccessful = false;
        }

        return new FileOpsSuccess(isSuccessful, savedFile);
    }

    public static FileOpsSuccess cacheGif(Bitmap bitmap, String dst, String filename){
        if (bitmap == null){
            return new FileOpsSuccess(false, null);
        }

        boolean isSuccessful;
        File savedFile = null;

        try {
            OutputStream os = null;
            try {
                File saveDir = new File(dst);

                if (!saveDir.exists()){
                    if (!saveDir.mkdir()){
                        return new FileOpsSuccess(false, null);
                    }
                }

                File saveFile = new File(saveDir.getAbsolutePath() + File.separator + filename);
                savedFile = saveFile;

                os = new FileOutputStream(saveFile);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);

                isSuccessful = true;
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
                isSuccessful = false;
            } finally {
                if (os != null){
                    os.close();
                }
            }
        } catch (IOException ioe){
            Log.e(TAG, ioe.getMessage());
            isSuccessful = false;
        }

        return new FileOpsSuccess(isSuccessful, savedFile);
    }

    public static boolean doesFileExist(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

    private static boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.d(TAG, "External storage is writable");
            return true;
        } else {
            Log.d(TAG, "External storage is not writable");
            return false;
        }
    }

    private static boolean isExternalStorageReadable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())){
            Log.d(TAG, "External storage is readable");
            return true;
        } else {
            Log.d(TAG, "External storage is readable");
            return false;
        }
    }
}
