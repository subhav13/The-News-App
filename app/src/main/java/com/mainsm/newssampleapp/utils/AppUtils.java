package com.mainsm.newssampleapp.utils;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName() ;


    public static String DateToTimeFormat(String oldstringDate, Context context){
        PrettyTime p = new PrettyTime(new Locale(getCountry(context)));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                        Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    public static String DateFormat(String oldstringDate, Context context){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry(context)));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry(Context context){
        String country = context.getResources().getConfiguration().locale.getCountry();
        Log.e(TAG, "getCountry: " + country);
        return country.toLowerCase();
    }



}
