package com.techhive.statussaver.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPrefs {
    public static final String PREF_NIGHT_MODE = "night_mode";
    private static SharedPreferences mPreferences;
    public static final String WA_TREE_URI = "wa_tree_uri";
    public static final String WB_TREE_URI = "wb_tree_uri";
    public static final String LANGUAGE = "language";
    public static final String ALL_SAVER_DATA = "all_saver_data";
    public static final String ENGLISH_LOCALE = "en_US";
    public static final String GUJARATI_LOCALE = "gu";
    public static final String HINDI_LOCALE = "hi";
    public static final String IS_PRO = "isPro";

    public static String Adshow = "Adshow";

    public static String showInterstitial = "showInterstitial";

    public static String bannerAdId = "bannerAdId";

    public static String interstitialAdId = "interstitialAdId";
    public static String nativeAdId = "nativeAdId";


    public static SharedPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getApplicationContext()
                    .getSharedPreferences(ALL_SAVER_DATA, Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static void saveBooleanValue(Context context,String key, boolean value) {
        getInstance(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanValue(Context context,String key) {
        return getInstance(context).getBoolean(key, false);
    }

    public static void saveStringValue(Context context,String key, String value) {
        getInstance(context).edit().putString(key, value).apply();
    }

    public static String getStringValue(Context context, String key) {
        return getInstance(context).getString(key, "");
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getInstance(context).getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        getInstance(context).edit().putInt(key, value).apply();
    }

    public static void clearPrefs(Context context) {
        getInstance(context).edit().clear().apply();
    }

    public static int getAppNightDayMode(Context context) {
        return getInt(context, PREF_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setWATree(Context context, String value) {
        getInstance(context).edit().putString(WA_TREE_URI, value).apply();
    }

    public static String getWATree(Context context) {
        return getInstance(context).getString(WA_TREE_URI, "");
    }

    public static void setWBTree(Context context, String value) {
        getInstance(context).edit().putString(WB_TREE_URI, value).apply();
    }

    public static String getWBTree(Context context) {
        return getInstance(context).getString(WB_TREE_URI, "");
    }

    public static String getLanguage(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ALL_SAVER_DATA, Context.MODE_PRIVATE);
        return pref.getString(LANGUAGE, "");
    }

    public static void setLanguage(Context context, String language) {
        SharedPreferences pref = context.getSharedPreferences(ALL_SAVER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public static boolean getIsPro(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ALL_SAVER_DATA, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_PRO, false);
    }

    public static void setIsPro(Context context, boolean isPro) {
        SharedPreferences pref = context.getSharedPreferences(ALL_SAVER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_PRO, isPro);
        editor.apply();
    }
}
