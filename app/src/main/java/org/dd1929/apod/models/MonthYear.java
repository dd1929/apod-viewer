package org.dd1929.apod.models;

import android.content.Context;

import org.dd1929.apod.utils.DateParseUtils;
import org.dd1929.apod.utils.FormatUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MonthYear implements Serializable {
    private Integer mMonth;
    private Integer mYear;

    public MonthYear(int month, int year) {
        mMonth = month;
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public Date getFirstOfMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, 1);
        calendar.setTime(DateParseUtils.getDateFromString(DateParseUtils.getStringFromDate(calendar.getTime())));
        return calendar.getTime();
    }

    public Date getLastOfMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.setTime(DateParseUtils.getDateFromString(DateParseUtils.getStringFromDate(calendar.getTime())));
        return calendar.getTime();
    }

    public MonthYear getPrevMonth(){
        Integer prevMonth, prevYear;
        if (mMonth > Calendar.JANUARY){
            prevMonth = mMonth - 1;
            prevYear = mYear;
        } else {
            prevMonth = Calendar.DECEMBER;
            prevYear = mYear - 1;
        }
        return new MonthYear(prevMonth, prevYear);
    }

    public MonthYear getNextMonth(){
        Integer nextMonth, nextYear;
        if (mMonth < Calendar.DECEMBER){
            nextMonth = mMonth + 1;
            nextYear = mYear;
        } else {
            nextMonth = Calendar.JANUARY;
            nextYear = mYear + 1;
        }
        return new MonthYear(nextMonth, nextYear);
    }

    public static MonthYear fromString(String monthYearString){
        if (monthYearString == null){
            return null;
        } else {
            String[] fields = monthYearString.split(" ");
            return new MonthYear(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
        }

    }

    @Override
    public String toString() {
        return mMonth + " " + mYear;
    }
}
