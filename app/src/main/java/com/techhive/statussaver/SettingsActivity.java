package com.techhive.statussaver;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.techhive.statussaver.databinding.ActivitySettingsBinding;
import com.techhive.statussaver.utils.Helpers;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySettingsBinding binding;
    Context mContext;
    private Activity mActivity;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = mActivity = this;


        binding.modeSwitch.setChecked(SharedPrefs.getBooleanValue(SettingsActivity.this, SharedPrefs.PREF_NIGHT_MODE));
        binding.modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                SharedPrefs.saveBooleanValue(SettingsActivity.this, SharedPrefs.PREF_NIGHT_MODE, true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                SharedPrefs.saveBooleanValue(SettingsActivity.this, SharedPrefs.PREF_NIGHT_MODE, false);
            }
        });


        binding.moreapp.setOnClickListener(this);
        binding.policy.setOnClickListener(this);
        binding.shareapp.setOnClickListener(this);
        binding.rateapp.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
        binding.txtSelectedLang.setOnClickListener(this);
        binding.historyApp.setOnClickListener(this);



        /*-------------------change language--------------------
         *This declaration is check language set in preference then convert to that lang.
         * if not then default set English........................*/


        String lang = SharedPrefs.getLanguage(mContext);
        Log.e("lang", lang);
        if (lang.equals("")) {
            SharedPrefs.setLanguage(mContext, SharedPrefs.ENGLISH_LOCALE);
            lang = SharedPrefs.getLanguage(mContext);
        }
        switch (lang) {
            case SharedPrefs.ENGLISH_LOCALE:
                locale = new Locale(SharedPrefs.ENGLISH_LOCALE);
                binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.english));
                break;
            case SharedPrefs.GUJARATI_LOCALE:
                locale = new Locale(SharedPrefs.GUJARATI_LOCALE);
                binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.gujarti));
                break;
            case SharedPrefs.HINDI_LOCALE:
                locale = new Locale(SharedPrefs.HINDI_LOCALE);
                binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.hindi));
                break;
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration,
                getResources().getDisplayMetrics());
//        onConfigurationChanged(configuration);
        /*-----------------------------------------------------*/
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        binding.txtDarkMode.setText(mContext.getResources().getString(R.string.dark_mode));
        binding.txtRateUs.setText(mContext.getResources().getString(R.string.rate_us));
        binding.txtLanguage.setText(mContext.getResources().getString(R.string.language));
        binding.txtShareApp.setText(mContext.getResources().getString(R.string.share_app));
        binding.txtPrivacyPolicy.setText(mContext.getResources().getString(R.string.privacy_policy));
        binding.txtMoreApp.setText(mContext.getResources().getString(R.string.more_apps));
        binding.txtHistory.setText(mContext.getResources().getString(R.string.history));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backBtn) {
            onBackPressed();
        } else if (id == R.id.rateapp) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.shareapp) {
            Helpers.mShareText("Hey my friend check out this app\n https://play.google.com/store/apps/details?id=" + getPackageName() + " \n", SettingsActivity.this);

        } else if (id == R.id.policy) {
            startActivity(new Intent(SettingsActivity.this, PrivacyActivity.class));
        } else if (id == R.id.moreapp) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps_url))));
            } catch (Exception e) {

            }

        } else if (id == R.id.txtSelectedLang) {
            changeLanguageDialog();
        } else if (id == R.id.historyApp) {
            startActivity(new Intent(SettingsActivity.this, HistoryActivity.class));
        }
    }

    private void changeLanguageDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.language_alert_layout);

        ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);
        TextView txtEnglish = (TextView) dialog.findViewById(R.id.txtEnglish);
        TextView txtHindi = (TextView) dialog.findViewById(R.id.txtHindi);
        TextView txtGuj = (TextView) dialog.findViewById(R.id.txtGujarati);

        if (SharedPrefs.getLanguage(mContext).equals(SharedPrefs.ENGLISH_LOCALE)) {
            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        } else if (SharedPrefs.getLanguage(mContext).equals(SharedPrefs.HINDI_LOCALE)) {
            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        } else if (SharedPrefs.getLanguage(mContext).equals(SharedPrefs.GUJARATI_LOCALE)) {
            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        txtHindi.setOnClickListener(v -> {
            binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.hindi));

            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));


            Locale locale = new Locale(SharedPrefs.HINDI_LOCALE);
            SharedPrefs.setLanguage(mContext, SharedPrefs.HINDI_LOCALE);
            Utils.changeLanguage(mContext, locale);
            dialog.dismiss();
        });
        txtEnglish.setOnClickListener(v -> {
            binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.english));

            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            Locale locale1 = new Locale(SharedPrefs.ENGLISH_LOCALE);
            SharedPrefs.setLanguage(mContext, SharedPrefs.ENGLISH_LOCALE);
            Utils.changeLanguage(mContext, locale1);
            dialog.dismiss();
        });
        txtGuj.setOnClickListener(v -> {
            binding.txtSelectedLang.setText(mContext.getResources().getString(R.string.gujarti));

            txtHindi.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtHindi.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            txtGuj.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            txtGuj.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

            txtEnglish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            txtEnglish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            Locale locale1 = new Locale(SharedPrefs.GUJARATI_LOCALE);
            SharedPrefs.setLanguage(mContext, SharedPrefs.GUJARATI_LOCALE);
            Utils.changeLanguage(mContext, locale1);
            dialog.dismiss();
        });
        imgClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}