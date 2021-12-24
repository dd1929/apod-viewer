package org.dd1929.apod.utils;

import android.net.Uri;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParseUtils {

    public static Date getDateFromString(String dateString){
        if (dateString == null){
            return null;
        }
        String mDateString = dateString;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(mDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringFromDate(Date date){
        if (date == null){
            return null;
        }
        CharSequence dateFormat = "yyyy-MM-dd";
        String dateString = DateFormat.format(dateFormat, date).toString();
        return dateString;
    }

    public static String[] getMonthlyDates(Date date){
        String[] dates = new String[2];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (day != 1){
            calendar.add(Calendar.DAY_OF_MONTH, -(day-1));
            dates[0] = getStringFromDate(calendar.getTime());
        } else {
            dates[0] = getStringFromDate(calendar.getTime());
        }

        calendar.add(Calendar.DAY_OF_MONTH, maxDays-1);
        dates[1] = getStringFromDate(calendar.getTime());

        return dates;
    }

    public static Uri getUrlFromDate(Date date){
        String url = "https://apod.nasa.gov/apod/ap";
        CharSequence dateFormat = "yyMMdd";
        String dateAndFormat = DateFormat.format(dateFormat, date).toString() + ".html";
        return Uri.parse(url + dateAndFormat);
    }

}
