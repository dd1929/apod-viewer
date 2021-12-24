package org.dd1929.apod.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import org.dd1929.apod.adapters.APODAdapter;
import org.dd1929.apod.dialogs.GenericMessageFragment;
import org.dd1929.apod.dialogs.ImageQualityFragment;
import org.dd1929.apod.executors.FileOpsExecutor;
import org.dd1929.apod.models.APOD;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class APODUtils {

    private static final String TAG = "APODUtils";
    private static final String DIALOG_SET_AS = "SetAs";
    private static final String DIALOG_SAVE = "Save";
    private static final String DIALOG_SHARE = "Share";
    private static final String DIALOG_PERM_DENIED = "PermissionDenied";
    private static final String DIALOG_PERM_RATIONALE = "PermissionRationale";

    public static void showPermissionReasonDialog(FragmentManager manager, Context context){
        GenericMessageFragment dialog = GenericMessageFragment.newInstance(context.getString(R.string.storage_permission_denied),
                () -> {}); //GenericMessageFragment.OnChooseOkListener
        dialog.show(manager, DIALOG_PERM_DENIED);
    }

    public static void onClickSetAs(APOD apod, Context context, FragmentManager manager, View view){
        if (!(apod.getHdUrl() == null || apod.getHdUrl().equals(apod.getUrl()) || apod.getHdUrl().equals(""))){
            String quality = AppPreferences.getPrefQualitySetAs(context);
            Log.d(TAG, "Quality: "  + quality);
            if (quality.equals("low")){
                setImageAs(apod, "low", context, view);
            } else if (quality.equals("high")){
                setImageAs(apod, "high", context, view);
            } else {

                ImageQualityFragment dialog = ImageQualityFragment.newInstance((qual, savePrefs) -> { //ImageQualityFragment.OnChooseListener
                    if (qual.equals("low")){
                        setImageAs(apod, "low", context, view);
                    } else if (qual.equals("high")){
                        setImageAs(apod, "high", context, view);
                    }

                    if (savePrefs){
                        AppPreferences.setPrefQualitySave(context, qual);
                    }
                }, quality);
                dialog.show(manager, DIALOG_SET_AS);
            }
        } else {
            setImageAs(apod, "low", context, view);
        }
    }

    public static void setImageAs(APOD apod, String quality, Context context, View view){
        if (apod == null){
            return;
        }

        String url;
        if ("high".equals(quality)) {
            url = apod.getHdUrl();
        } else {
            url = apod.getUrl();
        }
        makeSnackbar(context.getString(R.string.getting_image), view);

        String dstPath = context.getFilesDir().getAbsolutePath();
        String filename = FormatUtils.generateFilename(false, apod, context)
                + "."
                + FormatUtils.getFileFormat(url);

        if (!StorageUtils.doesFileExist(dstPath + File.separator + filename)){
            if (!FormatUtils.getFileFormat(url).equals("gif")){
                Glide.with(context).asFile().load(url).into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull File resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super File> transition) {
                        FileOpsExecutor.getInstance().cacheFile(resource,
                                context.getFilesDir().getAbsolutePath(),
                                filename,
                                context.getApplicationContext().getContentResolver(),
                                (isSuccessful, file) -> { //FileOpsExecutor.OnCompletionListener
                                    if (isSuccessful && file != null){
                                        IntentUtils.setAs(file, context);
                                    } else {
                                        makeSnackbar(context.getString(R.string.image_set_as_unsuccessful), view);
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });
            } else {
                Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        FileOpsExecutor.getInstance().cacheGif(resource,
                                dstPath,
                                filename,
                                (isSuccessful, file) -> { //FileOpsExecutor.OnCompletionListener
                                    if (isSuccessful && file != null){
                                        IntentUtils.setAs(file, context);
                                    } else {
                                        makeSnackbar(context.getString(R.string.image_share_unsuccessful), view);
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }
                });
            }
        } else {
            IntentUtils.setAs(new File(dstPath + File.separator + filename), context);
        }
    }

    public static void onClickShare(APOD apod, Context context, FragmentManager manager, View view){
        if (apod.getMediaType().equals("image")){
            if (!(apod.getHdUrl() == null || apod.getHdUrl().equals(apod.getUrl()) || apod.getHdUrl().equals(""))){
                String quality = AppPreferences.getPrefQualityShare(context);
                Log.d(TAG, "Quality: "  + quality);
                if (quality.equals("low")){
                    shareImage(apod, "low", context, view);
                } else if (quality.equals("high")){
                    shareImage(apod, "high", context, view);
                } else {
                    ImageQualityFragment dialog = ImageQualityFragment.newInstance((qual, savePrefs) -> { //ImageQualityFragment.OnChooseListener
                        if (qual.equals("low")){
                            shareImage(apod, "low", context, view);
                        } else if (qual.equals("high")){
                            shareImage(apod, "high", context, view);
                        }

                        if (savePrefs){
                            AppPreferences.setPrefQualitySave(context, qual);
                        }
                    }, quality);
                    dialog.show(manager, DIALOG_SHARE);
                }
            } else {
                shareImage(apod, "low", context, view);
            }
        } else {
            String shareText = apod.getUrl() + "\n\n" + apod.getTitle() + "\n\n" + apod.getDetails();
            IntentUtils.shareOther(shareText, context);
        }
    }

    public static void shareImage(APOD apod, String quality, Context context, View view){
        if (apod == null){
            return;
        }

        String url;
        if ("high".equals(quality)) {
            url = apod.getHdUrl();
        } else {
            url = apod.getUrl();
        }
        makeSnackbar(context.getString(R.string.getting_image), view);

        String dstPath = context.getFilesDir().getAbsolutePath();
        String filename = FormatUtils.generateFilename(false, apod, context)
                + "."
                + FormatUtils.getFileFormat(url);

        if (!StorageUtils.doesFileExist(dstPath + File.separator + filename)){
            Glide.with(context).asFile().load(url).into(new CustomTarget<File>() {
                @Override
                public void onResourceReady(@NonNull @NotNull File resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super File> transition) {
                    FileOpsExecutor.getInstance().cacheFile(resource,
                            dstPath,
                            filename,
                            context.getApplicationContext().getContentResolver(),
                            (isSuccessful, file) -> { //FileOpsExecutor.OnCompletionListener
                                if (isSuccessful && file != null){
                                    IntentUtils.shareImage(file, context, FormatUtils.generateShareText(apod, context));
                                } else {
                                    makeSnackbar(context.getString(R.string.image_share_unsuccessful), view);
                                }
                            });
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });
        } else {
            IntentUtils.shareImage(new File(dstPath + File.separator + filename), context, FormatUtils.generateShareText(apod, context));
        }

    }

    public static void onClickSave(APOD apod, Context context, FragmentManager manager, View view, ActivityResultLauncher<String> launcher){
        if (!PermissionUtils.checkWriteStoragePermission(context)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (((AppCompatActivity) context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    GenericMessageFragment dialog = GenericMessageFragment.newInstance(context.getString(R.string.storage_permission_rationale),
                            () -> launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)); //GenericMessageFragment.OnChooseOkListener
                    dialog.show(manager, DIALOG_PERM_RATIONALE);
                } else {
                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                showPermissionReasonDialog(manager, context);
            }
            return;
        }

        if (!(apod.getHdUrl() == null || apod.getHdUrl().equals(apod.getUrl()) || apod.getHdUrl().equals(""))){
            String quality = AppPreferences.getPrefQualitySave(context);
            Log.d(TAG, "Quality: "  + quality);
            if (quality.equals("low")){
                downloadImage(apod, "low", context, view);
            } else if (quality.equals("high")){
                downloadImage(apod, "high", context, view);
            } else {
                ImageQualityFragment dialog = ImageQualityFragment.newInstance(new ImageQualityFragment.OnChooseListener() {
                    @Override
                    public void onOptionChosen(String qual, boolean savePrefs) {
                        if (qual.equals("low")){
                            downloadImage(apod, "low", context, view);
                        } else if (qual.equals("high")){
                            downloadImage(apod, "high", context, view);
                        }

                        if (savePrefs){
                            AppPreferences.setPrefQualitySave(context, qual);
                        }
                    }
                }, quality);
                dialog.show(manager, DIALOG_SAVE);
            }
        } else {
            downloadImage(apod, "low", context, view);
        }
    }

    private static void downloadImage(APOD apod, String quality, Context context, View view){
        if (apod == null){
            return;
        }

        String url;
        if (quality.equals("high")){
            url = apod.getHdUrl();
        } else {
            url = apod.getUrl();
        }
        makeSnackbar(context.getString(R.string.saving_image), view);

        Glide.with(context).asFile().load(url).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull @NotNull File resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super File> transition) {
                FileOpsExecutor.getInstance().saveFile(resource,
                        FormatUtils.generateFilename(true, apod, context.getApplicationContext()),
                        FormatUtils.getMimeType(FormatUtils.getFileFormat(url)),
                        context.getApplicationContext().getContentResolver(),
                        (isSuccessful, file) -> { //FileOpsExecutor.OnCompletionListener
                            if (isSuccessful){
                                makeSnackbar(context.getString(R.string.image_save_successful), view);
                            } else {
                                makeSnackbar(context.getString(R.string.image_save_unsuccessful), view);
                            }
                        });
            }

            @Override
            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

            }
        });
    }

    public static void makeSnackbar(String text, View view){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    // Methods for RecyclerView Fragments (MonthlyFragment, BookmarksFragment, DiscoverFragment, SearchFragment)
    public static void setAPODsOnAdapter(Context context, List<APOD> apods, APODAdapter adapter){
        if (apods.size() <= 0){
            return;
        }

        List<APOD> apodsToSet = new ArrayList<>();
        apodsToSet.addAll(apods);

        //Filtering APODs
        String filterMediaCriteria = AppPreferences.getPrefFilterMediaType(context);
        String filterCopyrightCriteria = AppPreferences.getPrefFilterCopyright(context);

        if (!filterMediaCriteria.contains(AppPreferences.IMAGE)){
            Iterator<APOD> apodIterator = apodsToSet.iterator();
            while (apodIterator.hasNext()){
                APOD apod = apodIterator.next();
                if (apod.getMediaType().equals("image")){
                    apodIterator.remove();
                }
            }
        }
        if (!filterMediaCriteria.contains(AppPreferences.VIDEO)){
            Iterator<APOD> apodIterator = apodsToSet.iterator();
            while (apodIterator.hasNext()){
                APOD apod = apodIterator.next();
                if (!apod.getMediaType().equals("image")){
                    apodIterator.remove();
                }
            }
        }

        if (!filterCopyrightCriteria.contains(AppPreferences.WITH_CR)){
            Iterator<APOD> apodIterator = apodsToSet.iterator();
            while (apodIterator.hasNext()){
                APOD apod = apodIterator.next();
                if (apod.getCopyright() != null){
                    apodIterator.remove();
                }
            }
        }
        if (!filterCopyrightCriteria.contains(AppPreferences.WITHOUT_CR)){
            Iterator<APOD> apodIterator = apodsToSet.iterator();
            while (apodIterator.hasNext()){
                APOD apod = apodIterator.next();
                if (apod.getCopyright() == null){
                    apodIterator.remove();
                }
            }
        }

        //Sorting APODs
        String sortByCriteria = AppPreferences.getPrefSortBy(context);
        String sortOrder = AppPreferences.getPrefSortOrder(context);

        switch (sortByCriteria){
            case AppPreferences.TITLE:
                Collections.sort(apodsToSet, (a1, a2) -> a1.getTitle().compareToIgnoreCase(a2.getTitle()));
                if (sortOrder.equals(AppPreferences.DESC)){
                    Collections.reverse(apodsToSet);
                }
                break;
            case AppPreferences.COPYRIGHT:
                Collections.sort(apodsToSet, (a1, a2) -> a1.getCopyright().compareToIgnoreCase(a2.getCopyright()));
                if (sortOrder.equals(AppPreferences.DESC)){
                    Collections.reverse(apodsToSet);
                }
                break;
            case AppPreferences.DETAILS:
                Collections.sort(apodsToSet, (a1, a2) -> a1.getDetails().compareToIgnoreCase(a2.getDetails()));
                if (sortOrder.equals(AppPreferences.DESC)){
                    Collections.reverse(apodsToSet);
                }
                break;
            default:
                Collections.sort(apodsToSet, (a1, a2) -> a1.getDate().compareTo(a2.getDate()));
                if (sortOrder.equals(AppPreferences.DESC)){
                    Collections.reverse(apodsToSet);
                }
        }

        adapter.setAPODs(apodsToSet);
        adapter.notifyDataSetChanged();
    }
}
