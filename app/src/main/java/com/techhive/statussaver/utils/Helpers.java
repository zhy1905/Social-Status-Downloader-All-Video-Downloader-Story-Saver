package com.techhive.statussaver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Environment;


import androidx.core.app.ActivityCompat;

import java.io.File;


public class Helpers {

    public static int position = 0;

    public static final String DIRECTORY_NAME = "AllStatSaver";

    public static void createDirectory() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + DIRECTORY_NAME);

        if (!path.exists()) {
            File file = new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME);
            file.mkdir();
        }
    }


    public static void mShareText(String text, Activity activity) {
        Intent myapp = new Intent(Intent.ACTION_SEND);
        myapp.setType("text/plain");
        myapp.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(myapp);
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
