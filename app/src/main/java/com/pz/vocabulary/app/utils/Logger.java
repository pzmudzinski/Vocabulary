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
}
