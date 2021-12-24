package org.dd1929.apod.executors;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;

import org.dd1929.apod.models.FileOpsSuccess;
import org.dd1929.apod.utils.StorageUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileOpsExecutor {

    private static final int NUMBER_OF_THREADS = 2;

    private static ExecutorService sExecutorService;
    private static FileOpsExecutor sExecutor;

    public interface OnCompletionListener{
        void onCompletion(boolean isSuccessful, File file);
    }

    public static FileOpsExecutor getInstance(){
        if (sExecutor == null){
            sExecutor = new FileOpsExecutor();
        }

        return sExecutor;
    }

    private FileOpsExecutor(){
        sExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public static void stop(){
        if (sExecutorService != null){
            sExecutorService.shutdown();
        }
    }

    public void saveFile(File resource, String filename, String mimeType, ContentResolver contentResolver, OnCompletionListener listener){
        sExecutorService.execute(() -> { //Runnable
            FileOpsSuccess fileOpsSuccess = StorageUtils.saveFile(resource, filename, mimeType, contentResolver);
            listener.onCompletion(fileOpsSuccess.isSuccessful(), fileOpsSuccess.getFile());
        });
    }

    public void cacheFile(File resource, String dst, String filename, ContentResolver resolver, OnCompletionListener listener){
        sExecutorService.execute(() -> { //Runnable
            FileOpsSuccess fileOpsSuccess = StorageUtils.cacheFile(resource, dst, filename, resolver);
            listener.onCompletion(fileOpsSuccess.isSuccessful(), fileOpsSuccess.getFile());
        });
    }

    public void cacheGif(Bitmap bitmap, String filename, String dst, OnCompletionListener listener){
        sExecutorService.execute(() -> { //Runnable
            FileOpsSuccess fileOpsSuccess = StorageUtils.cacheGif(bitmap, dst, filename);
            listener.onCompletion(fileOpsSuccess.isSuccessful(), fileOpsSuccess.getFile());
        });
    }

    public void clearGlideCache(Context context, OnCompletionListener listener){
        sExecutorService.execute(() -> {
            Glide.get(context).clearDiskCache();
            listener.onCompletion(true, null);
        });
    }

}
