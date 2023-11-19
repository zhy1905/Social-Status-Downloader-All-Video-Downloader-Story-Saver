package com.techhive.statussaver.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final int FileNameCount_VARIABLE = 0;
    public static final String  CURRENT_DOWNLOAD_VAR="currentdownloadvar";
    private static final String PREF_NAME = "statusSaverApp";
    private static String fileName="fileName";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }
    public int getFileName() {
        return pref.getInt(fileName, FileNameCount_VARIABLE);
    }

    public  void setFileName(int fileNames) {
        editor = pref.edit();
        editor.putInt(fileName, fileNames);
        editor.commit();
    }




}
