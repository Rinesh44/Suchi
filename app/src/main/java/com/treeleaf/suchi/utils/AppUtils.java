package com.treeleaf.suchi.utils;

import android.util.Log;

public class AppUtils {
    public static void showLog(String tag, String msg){
        if(tag != null && msg != null)
            Log.d(tag, "APP_FLOW --> " + msg);
    }
}
