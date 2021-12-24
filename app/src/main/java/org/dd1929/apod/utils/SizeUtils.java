package org.dd1929.apod.utils;

import android.content.Context;
import android.util.TypedValue;

public class SizeUtils {

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
