package org.dd1929.apod.preferences;

/* APOD - A simple app to view images from NASA's APOD service
    Copyright (C) 2021  Deepto Debnath

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see https://www.gnu.org/licenses/ */

import android.content.Context;

import androidx.preference.PreferenceManager;

public class AppPreferences {

    public static final String DATE = "date";
    public static final String TITLE = "title";
    public static final String COPYRIGHT = "copyright";
    public static final String DETAILS = "details";
    public static final String ASC = "ascending";
    public static final String DESC = "descending";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String WITH_CR = "withCopyright";
    public static final String WITHOUT_CR = "withoutCopyright";

    private static final String PREF_API_KEY = "apiKey";
    private static final String PREF_LATEST_APOD_DATE = "latestApodDate";
    private static final String PREF_VIEW = "view";
    private static final String PREF_THEME = "theme";
    private static final String PREF_MONTH_YEAR = "monthYear";
    private static final String PREF_LIST_APODS = "listOfAPODs";
    private static final String PREF_RANDOM_COUNT = "count";
    private static final String PREF_NUM_COLUMNS = "numColumns";
    private static final String PREF_BG_UPDATES = "updates";
    private static final String PREF_NOTIFICATIONS = "notifications";
    private static final String PREF_SET_AUTO_WALLPAPER = "wallpaper";
    private static final String PREF_QUALITY_AUTO_WALLPAPER = "autoWallpaperQuality";
    private static final String PREF_WALLPAPER_TARGET = "wallpaperTarget";
    private static final String PREF_QUALITY_SHARE = "shareQuality";
    private static final String PREF_QUALITY_SAVE= "saveQuality";
    private static final String PREF_QUALITY_SET_AS = "setAsQuality";
    private static final String PREF_FONT_SIZE = "fontSize";
    private static final String PREF_FONT_TYPE = "fontType";
    private static final String PREF_SAVE_FILENAME = "saveFilename";
    private static final String PREF_SHARE_FORMAT = "shareFormat";
    private static final String PREF_SORT_BY = "sortBy";
    private static final String PREF_SORT_ORDER = "sortOrder";
    private static final String PREF_FILTER_MEDIA_TYPE = "filterMediaType";
    private static final String PREF_FILTER_COPYRIGHT = "filterCopyright";
    private static final String PREF_SELECTED_MONTH = "selectedMonth";

    public static String getPrefApiKey(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_API_KEY, null);
    }

    public static void setPrefApiKey(Context context, String key){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_API_KEY, key).apply();
    }

    public static Long getLatestAPODDate(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PREF_LATEST_APOD_DATE, 0);
    }

    public static void setLatestApodDate(Context context, Long date){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PREF_LATEST_APOD_DATE, date).apply();
    }

    public static String getPrefView(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_VIEW, "grid");
    }

    public static void setPrefView(Context context, String view){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_VIEW, view).apply();
    }

    public static String getPrefTheme(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_THEME, "system");
    }

    public static void setPrefTheme(Context context, String theme){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_THEME, theme).apply();
    }

    public static String getPrefMonthYear(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_MONTH_YEAR, null);
    }

    public static void setPrefMonthYear(Context context, String monthYear){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_MONTH_YEAR, monthYear).apply();
    }

    public static String getPrefAPODList(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LIST_APODS, null);
    }

    public static void setPrefAPODList(Context context, String listPref){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LIST_APODS, listPref).apply();
    }

    public static int getPrefRandomCount(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_RANDOM_COUNT, 30);
    }

    public static void setPrefRandomCount(Context context, int count){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_RANDOM_COUNT, count).apply();
    }

    public static int getPrefNumColumns(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_NUM_COLUMNS, 0);
    }

    public static void setPrefNumColumns(Context context, int count){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_NUM_COLUMNS, count).apply();
    }

    public static Boolean getPrefBgUpdates(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_BG_UPDATES, true);
    }

    public static void setPrefBgUpdates(Context context, Boolean bool){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_BG_UPDATES, bool).apply();
    }

    public static Boolean getPrefNotifications(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_NOTIFICATIONS, true);
    }

    public static void setPrefNotifications(Context context, Boolean bool){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_NOTIFICATIONS, bool).apply();
    }

    public static Boolean getPrefSetAutoWallpaper(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_SET_AUTO_WALLPAPER, false);
    }

    public static void setPrefSetAutoWallpaper(Context context, Boolean bool){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_SET_AUTO_WALLPAPER, bool).apply();
    }

    public static String getPrefQualityAutoWallpaper(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_QUALITY_AUTO_WALLPAPER, "low");
    }

    public static void setPrefQualityAutoWallpaper(Context context, String quality){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_QUALITY_AUTO_WALLPAPER, quality).apply();
    }

    public static String getPrefWallpaperTarget(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_WALLPAPER_TARGET, "both");
    }

    public static void setPrefWallpaperTarget(Context context, String target){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_WALLPAPER_TARGET, target).apply();
    }

    public static String getPrefQualityShare(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_QUALITY_SHARE, "ask");
    }

    public static void setPrefQualityShare(Context context, String quality){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_QUALITY_SHARE, quality).apply();
    }

    public static String getPrefQualitySave(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_QUALITY_SAVE, "ask");
    }

    public static void setPrefQualitySave(Context context, String quality){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_QUALITY_SAVE, quality).apply();
    }

    public static String getPrefQualitySetAs(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_QUALITY_SET_AS, "ask");
    }

    public static void setPrefQualitySetAs(Context context, String quality){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_QUALITY_SET_AS, quality).apply();
    }

    public static int getPrefFontSize(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_FONT_SIZE, 14);
    }

    public static void setPrefFontSize(Context context, int size){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_FONT_SIZE, size).apply();
    }

    public static String getPrefFontType(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FONT_TYPE, "normal");
    }

    public static void setPrefFontType(Context context, String type){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_FONT_TYPE, type).apply();
    }

    public static String getPrefSaveFilename(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SAVE_FILENAME, "APOD_<DATE>");
    }

    public static void setPrefSaveFilename(Context context, String filename){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SAVE_FILENAME, filename).apply();
    }

    public static String getPrefShareFormat(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SHARE_FORMAT, "<TITLE>\n\n<DETAILS>");
    }

    public static void setPrefShareFormat(Context context, String shareFormat){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SHARE_FORMAT, shareFormat).apply();
    }

    public static String getPrefSortBy(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SORT_BY, DATE);
    }

    public static void setPrefSortBy(Context context, String sortBy){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SORT_BY, sortBy).apply();
    }

    public static String getPrefSortOrder(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SORT_ORDER, DESC);
    }

    public static void setPrefSortOrder(Context context, String sortOrder){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SORT_ORDER, sortOrder).apply();
    }

    public static String getPrefFilterMediaType(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FILTER_MEDIA_TYPE, ", " + IMAGE + ", " + VIDEO);
    }

    public static void setPrefFilterMediaType(Context context, String mediaTypeFilter){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_FILTER_MEDIA_TYPE, mediaTypeFilter).apply();
    }

    public static String getPrefFilterCopyright(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FILTER_COPYRIGHT, ", " + WITH_CR + ", " + WITHOUT_CR);
    }

    public static void setPrefFilterCopyright(Context context, String copyrightFilter){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_FILTER_COPYRIGHT, copyrightFilter).apply();
    }

    public static String getPrefSelectedMonth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SELECTED_MONTH, null);
    }

    public static void setPrefSelectedMonth(Context context, String month){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SELECTED_MONTH, month).apply();
    }
}