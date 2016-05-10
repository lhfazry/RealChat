package com.fazrilabs.realchat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by blastocode on 5/9/16.
 */
public class PrefUtil {
    public static void setUsername(Context context, String username) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("USERNAME", "");
    }
}
