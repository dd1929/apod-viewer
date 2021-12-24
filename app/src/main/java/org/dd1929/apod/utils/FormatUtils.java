package org.dd1929.apod.utils;

import android.content.Context;

import org.dd1929.apod.models.APOD;
import org.dd1929.apod.preferences.AppPreferences;
import org.dd1929.apod.R;

import java.util.Date;

public class FormatUtils {

    public static String getFileFormat (String url){
        String format;
        String[] urlSplit = url.split("/");
        String originalFilename = urlSplit[urlSplit.length - 1];
        String[] filenameSplit = originalFilename.split("\\.");
        format = filenameSplit[filenameSplit.length - 1];
        return format;
    }

    public static String generateFilename(boolean asPerPref, APOD apod, Context context){
        String format = AppPreferences.getPrefSaveFilename(context);
        String filename;

        if (asPerPref){
            filename = format.replace("<TITLE>", apod.getTitle())
                    .replace("<DATE>", DateParseUtils.getStringFromDate(new Date(apod.getDate())))
                    .replace("<DETAILS>", apod.getDetails());

            if (filename.contains("<COPYRIGHT")){
                filename = filename.replace("<COPYRIGHT>", apod.getCopyright());
            }
        } else {
            filename = "APOD_" + DateParseUtils.getStringFromDate(new Date(apod.getDate()));
        }

        return filename;
    }

    public static String generateShareText(APOD apod, Context context){
        String format = AppPreferences.getPrefShareFormat(context);
        String shareText = format.replace("<TITLE>", apod.getTitle())
                .replace("<DATE>", DateParseUtils.getStringFromDate(new Date(apod.getDate())))
                .replace("<DETAILS>", apod.getDetails())
                .replace("<MEDIA_TYPE>", apod.getMediaType().toUpperCase());

        if (shareText.contains("<COPYRIGHT>")){
            shareText = shareText.replace("<COPYRIGHT>", apod.getCopyright());
        }

        return shareText;
    }

    public static String getMimeType(String format){
        switch (format){
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "image/*";

        }
    }

    public static String getMonthName(Context context, Integer month){
        String monthString = "Month";
        switch (month) {
            case 0:
                monthString = context.getString(R.string.january);
                break;
            case 1:
                monthString = context.getString(R.string.february);
                break;
            case 2:
                monthString = context.getString(R.string.march);
                break;
            case 3:
                monthString = context.getString(R.string.april);
                break;
            case 4:
                monthString = context.getString(R.string.may);
                break;
            case 5:
                monthString = context.getString(R.string.june);
                break;
            case 6:
                monthString = context.getString(R.string.july);
                break;
            case 7:
                monthString = context.getString(R.string.august);
                break;
            case 8:
                monthString = context.getString(R.string.september);
                break;
            case 9:
                monthString = context.getString(R.string.october);
                break;
            case 10:
                monthString = context.getString(R.string.november);
                break;
            case 11:
                monthString = context.getString(R.string.december);
                break;
        }

        return monthString;
    }

    public static int getMonthInt(Context context, String month){
        int monthInt = 0;
        if (context.getString(R.string.january).equals(month)) {
            monthInt = 0;
        } else if (context.getString(R.string.february).equals(month)) {
            monthInt = 1;
        } else if (context.getString(R.string.march).equals(month)) {
            monthInt = 2;
        } else if (context.getString(R.string.april).equals(month)) {
            monthInt = 3;
        } else if (context.getString(R.string.may).equals(month)) {
            monthInt = 4;
        } else if (context.getString(R.string.june).equals(month)) {
            monthInt = 5;
        } else if (context.getString(R.string.july).equals(month)) {
            monthInt = 6;
        } else if (context.getString(R.string.august).equals(month)) {
            monthInt = 7;
        } else if (context.getString(R.string.september).equals(month)) {
            monthInt = 8;
        } else if (context.getString(R.string.october).equals(month)) {
            monthInt = 9;
        } else if (context.getString(R.string.november).equals(month)) {
            monthInt = 10;
        } else if (context.getString(R.string.december).equals(month)) {
            monthInt = 11;
        }

        return monthInt;
    }
}
