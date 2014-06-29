package com.pz.vocabulary.app.utils;

import android.util.Log;

/**
 * Created by piotr on 04/06/14.
 */
public class Logger {

    public static void log(String tag, String msg)
    {
        //if (BuildConfig.DEBUG)
            Log.d(tag, msg);
    }

    public static void error(String tag, String msg)
    {
        Log.e(tag, msg);
    }

    public static void error(String tag, String msg, Throwable throwable)
    {
        Log.e(tag,msg, throwable);
    }
}
