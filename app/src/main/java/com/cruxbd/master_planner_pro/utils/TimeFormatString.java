package com.cruxbd.master_planner_pro.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeFormatString {

    public static String getStringTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(c.getTime());
        String total_date = "";
        if (c.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR))
            total_date = new SimpleDateFormat("MMM dd, yyyy").format(c.getTime());
        else
            total_date = new SimpleDateFormat("MMM dd").format(c.getTime());

        return (aTime + "\n" + dayOfTheWeek + ", " + total_date);
    }

    public static String getDateString(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(c.getTime());
        String total_date = "";
        if (c.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR))
            total_date = new SimpleDateFormat("MMM dd, yyyy").format(c.getTime());
        else
            total_date = new SimpleDateFormat("MMM dd").format(c.getTime());

        return (dayOfTheWeek + ", " + total_date);
    }

    public static String getTimeOnly(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();

        return aTime;

    }
}
