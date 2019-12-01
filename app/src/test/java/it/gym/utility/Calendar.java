package it.gym.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Calendar {

    public static Date getNextMonday() {
        java.util.Calendar date = java.util.Calendar.getInstance(Locale.ITALIAN);
        date.set(java.util.Calendar.HOUR_OF_DAY, 8);
        int diff = java.util.Calendar.MONDAY - date.get(java.util.Calendar.DAY_OF_WEEK);
        if (diff <= 0) {
            diff += 7;
        }
        date.add(java.util.Calendar.DAY_OF_MONTH, diff);
        return date.getTime();
    }

    public static String today(String pattern) {
        Date date = new Date();
        return format(date, pattern);
    }

    public static String yesterday(String pattern) {
        java.util.Calendar date = java.util.Calendar.getInstance(Locale.ITALIAN);
        int diff = date.get(java.util.Calendar.HOUR_OF_DAY);
        if (diff % 24 == 0) {
            diff = 23;
        }
        else {
            diff = 24;
        }
        date.add(java.util.Calendar.HOUR_OF_DAY, -diff);

        return format(date.getTime(), pattern);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
