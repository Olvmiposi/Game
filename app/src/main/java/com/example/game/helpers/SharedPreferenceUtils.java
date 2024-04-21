package com.example.game.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private Context mContext;
    public SharedPreferenceUtils(Context context)
    {
       this.mContext = context;
    }
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("myKey", Activity.MODE_PRIVATE);
            editor = prefs.edit();
        }
    }
    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    public String getString(String key, String defValue) {

        if (prefs == null) {
            prefs = this.mContext.getSharedPreferences("myKey", Activity.MODE_PRIVATE);
            editor = prefs.edit();
        }
        return prefs.getString(key, defValue);
    }
}