package com.techhive.statussaver.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.techhive.statussaver.utils.Utils;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        Utils.applyLanguage(newBase);
    }
}