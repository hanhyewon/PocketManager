package com.example.hyejin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created By sinhyeongseob on 2019-05-22
 */
public class Util {

    public static String format(Calendar cal, String pattern) {
        return format(cal.getTime(), pattern);
    }

    public static String format(Date date, String strToPattern) {
        return format(date, strToPattern, null);
    }

    public static String format(Date date, String strToPattern, Locale locale) {
        if (date == null || strToPattern == null)
            return null;

        SimpleDateFormat toFormat;
        if (locale != null) {
            toFormat = new SimpleDateFormat(strToPattern, locale);
        } else {
            toFormat = new SimpleDateFormat(strToPattern);
        }
        return toFormat.format(date);
    }

}
