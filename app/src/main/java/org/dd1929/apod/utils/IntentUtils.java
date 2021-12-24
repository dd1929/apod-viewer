package org.dd1929.apod.utils;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.dd1929.apod.R;

import java.io.File;

public class IntentUtils {

    private static final String FILE_PROVIDER = "org.dd1929.apod.fileprovider";

    public static void shareImage(File file, Context context, String text){
        Uri contentUri = FileProvider.getUriForFile(context, FILE_PROVIDER, file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setDataAndType(contentUri, "image/*");

        shareIntent.setClipData(ClipData.newRawUri("", contentUri));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.share_image)));
    }

    public static void shareOther(String text, Context context){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.share_image)));
    }

    public static void setAs(File file, Context context){
        Uri contentUri = FileProvider.getUriForFile(context, FILE_PROVIDER, file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_ATTACH_DATA);
        shareIntent.setDataAndType(contentUri, "image/*");

        shareIntent.setClipData(ClipData.newRawUri("", contentUri));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.set_as)));
    }

    public static void sendWebIntent(Uri url, Context context){
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(url);
        context.startActivity(webIntent);
    }

}
