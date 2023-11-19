package com.techhive.statussaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity {
    Context mContext;
    Locale locale;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = mActivity = this;
        setContentView(R.layout.activity_splash);

        Utils.applyLanguage(mContext);

//        live version
        initRemoteConfig();

//        test mode
        /*,....*/
//        SharedPrefs.saveBooleanValue(SplashActivity.this, SharedPrefs.Adshow, true);
//        SharedPrefs.saveBooleanValue(SplashActivity.this, SharedPrefs.showInterstitial, true);
//        SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.bannerAdId, getString(R.string.admob_banner_id));
//        SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.interstitialAdId, getString(R.string.admob_interstitial));
//        SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.nativeAdId, getString(R.string.admob_native));
        /*,....*/

        jumpToNext();

    }

    void jumpToNext() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }, 2000);
    }

    public void initRemoteConfig() {
        Log.d("databseConfig", "Task enter");
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                    Log.d("databseConfig", "Config params updated: " + updated);
                    SharedPrefs.saveBooleanValue(SplashActivity.this, SharedPrefs.Adshow, mFirebaseRemoteConfig.getBoolean(SharedPrefs.Adshow));
                    SharedPrefs.saveBooleanValue(SplashActivity.this, SharedPrefs.showInterstitial, mFirebaseRemoteConfig.getBoolean(SharedPrefs.showInterstitial));
                    SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.bannerAdId, mFirebaseRemoteConfig.getString(SharedPrefs.bannerAdId));
                    SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.interstitialAdId, mFirebaseRemoteConfig.getString(SharedPrefs.interstitialAdId));
                    SharedPrefs.saveStringValue(SplashActivity.this, SharedPrefs.nativeAdId, mFirebaseRemoteConfig.getString(SharedPrefs.nativeAdId));

                    Log.d("databseConfig", "onCreate: isAdShow " + SharedPrefs.getBooleanValue(SplashActivity.this, SharedPrefs.Adshow));
                    Log.d("databseConfig", "onCreate: showInterstitial " + SharedPrefs.getBooleanValue(SplashActivity.this, SharedPrefs.showInterstitial));
                    Log.d("databseConfig", "onCreate: banner_key " + SharedPrefs.getStringValue(SplashActivity.this, SharedPrefs.bannerAdId));
                    Log.d("databseConfig", "onCreate: interstitial_ad_key " + SharedPrefs.getStringValue(SplashActivity.this, SharedPrefs.interstitialAdId));
                    Log.d("databseConfig", "onCreate: native_ad_key " + SharedPrefs.getStringValue(SplashActivity.this, SharedPrefs.nativeAdId));

                } else {
                    Log.d("databseConfig", "onComplete: fetch failed");
                }
            }

        });
        mFirebaseRemoteConfig.fetchAndActivate().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("databseConfig", "onComplete: fetch failed");
            }
        });
    }
}
