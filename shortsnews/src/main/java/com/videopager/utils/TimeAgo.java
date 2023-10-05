package com.videopager.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeAgo {

    public final static String monthAgo =  " month ago";
    public final static String weekAgo =  " week ago";
    public final static String daysAgo =  " days ago";
    public final static String hoursAgo = " hour ago";
    public final static String minAgo =  " minute ago";
    public final static String secAgo = " seconds ago";
    static int second = 1000; // milliseconds
    static int minute = 60;
    static int hour = minute * 60;
    static int day = hour * 24;
    static int week = day * 7;
    static int month = day * 30;
    static int year = month * 12;

    @SuppressLint("SimpleDateFormat")
    public static String DateDifference(String fromDate) {
        long milliseconds = 0L;
        SimpleDateFormat f = new SimpleDateFormat("");
        try {
            Date d = f.parse(fromDate);
           milliseconds  = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = 0;
        long ms2 = System.currentTimeMillis();
        // get difference in milli seconds
        diff = ms2 - milliseconds;

        int diffInSec = Math.abs((int) (diff / (second)));
        String difference = "";
        if(diffInSec < minute)
        {
            difference = diffInSec+secAgo;
        }
        else if((diffInSec / hour) < 1)
        {
            difference = (diffInSec/minute)+minAgo;
        }
        else if((diffInSec/ day) < 1)
        {
            difference = (diffInSec/hour)+hoursAgo;
        }
        else if((diffInSec/ week) < 1)
        {
            difference = (diffInSec/day)+daysAgo;
        }
        else if((diffInSec/month)<1)
        {
            difference = (diffInSec / week)+weekAgo;
        }
        else if((diffInSec/year)<1)
        {
            difference = (diffInSec / month)+monthAgo;
        }
        else
        {
            // return date
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(milliseconds);

            SimpleDateFormat format_before = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

            difference = format_before.format(c.getTime());
        }


        Log.e("time difference is: ","" + difference);
        return difference;
    }
}
