package com.techhive.statussaver.utils;

import static com.techhive.statussaver.utils.SharedPrefs.ALL_SAVER_DATA;
import static com.techhive.statussaver.utils.SharedPrefs.PREF_NIGHT_MODE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

public class MyApp extends Application {
    public static MyApp instance;
//    private static final String ONESIGNAL_APP_ID = "########-####-####-####-############";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Verbose Logging set to help debug issues, remove before releasing your app.
       /* OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);*/

        SharedPreferences prefs = getSharedPreferences(ALL_SAVER_DATA, MODE_PRIVATE);
        boolean theme = prefs.getBoolean(PREF_NIGHT_MODE, false);
        Log.d("themestatus", "" + theme);
        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        try {
            initLibraries();
        } catch (YoutubeDLException e) {
            e.printStackTrace();
        }
    }

    private void initLibraries() throws YoutubeDLException {
        YoutubeDL.getInstance().init(this);
        FFmpeg.getInstance().init(this);
    }
}
