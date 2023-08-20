package com.example.d_five.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.d_five.utilities.PreferenceManager;

public class SaveSharedService {
    static final String PREF_DOMAIN= "domain";
    static final String PREF_USERNAME= "username";
    static final String PREF_PASSWORD= "password";
    static final String PREF_DARK_MODE= "darkmode";

    static final String PREF_FLASH = "flash";
    static final String PREF_RING = "ring";

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setDomain(Context ctx, String domain) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_DOMAIN, domain);
        editor.commit();
    }

    public static void setUsername(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USERNAME, userName);
        editor.commit();
    }

    public static void setPassword(Context ctx, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }

    public static void setFlash(Context ctx, Boolean isFlash) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_FLASH, isFlash);
        editor.commit();
    }

    public static void setRing(Context ctx, Boolean isFlash) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_RING, isFlash);
        editor.commit();
    }

    public static String getDomain(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOMAIN, "");
    }

    public static String getUsername(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USERNAME, "");
    }

    public static String getPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
    }
//    public static void setDarkMode(Context ctx, Boolean dark_mode)
//    {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.putBoolean(PREF_DARK_MODE, dark_mode);
//        editor.commit();
//    }
//
//    public static Boolean getDarkMode(Context ctx) {
//        return getSharedPreferences(ctx).getBoolean(PREF_DARK_MODE, false);
//    }
    public static Boolean getFlash(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_FLASH, false);
    }

    public static Boolean getRing(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_RING, false);
    }
}
