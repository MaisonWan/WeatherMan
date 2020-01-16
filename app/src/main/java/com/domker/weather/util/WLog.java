package com.domker.weather.util;

import android.util.Log;

import com.domker.weather.BuildConfig;

/**
 * Created by wanlipeng on 2019/2/5 7:03 PM
 */
public final class WLog {
    public static final String TAG = "Weather";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void i(String message) {
        if (DEBUG && message != null) {
            Log.i(TAG, message);
        }
    }

}
