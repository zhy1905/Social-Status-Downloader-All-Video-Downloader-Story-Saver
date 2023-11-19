package com.techhive.statussaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.techhive.statussaver.adapter.WAPagerAdapter;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.LayoutManager;
import com.techhive.statussaver.utils.SharedPrefs;

import java.io.File;

public class WABusiActivity extends AppCompatActivity implements View.OnClickListener {

    WAPagerAdapter adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    AdView mAdView;

    ImageView backBtn;
    ImageView waBusiBtn;

    Context mContext;
    private Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wawhats);
        mContext = mActivity= this;

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        String mBaseFolderPath = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.foldername) + File.separator;
        if (!new File(mBaseFolderPath).exists()) {
            boolean isMake = new File(mBaseFolderPath).mkdir();
            if (isMake) Log.v("File Create ", " Success");
        }

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        adapter = new WAPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);


        tabLayout = findViewById(R.id.sliding_tabs);
//        tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("OBJECT " + (position + 1))
        ).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) tab.setCustomView(getTabView(i));
        }

        View v = LayoutInflater.from(WABusiActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imageUnPress[0]);
        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WABusiActivity.this, 400, 110);
        img.setText(R.string.recent_status);
        img.setTextColor(ContextCompat.getColor(mContext, R.color.tab_press_text_color));
        img.setLayoutParams(param);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.setCustomView(null);
        tab.setCustomView(v);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                TabLayout.Tab tabs = tabLayout.getTabAt(tab.getPosition());
                tabs.setCustomView(null);
                tabs.setCustomView(getTabViewUn(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TabLayout.Tab tabs = tabLayout.getTabAt(tab.getPosition());
                tabs.setCustomView(null);
                tabs.setCustomView(getTabView(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);

        waBusiBtn = findViewById(R.id.waBusiBtn);
        waBusiBtn.setOnClickListener(this);
        if (!SharedPrefs.getIsPro(mActivity)) {
            if (SharedPrefs.getBooleanValue(mActivity, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(WABusiActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));

            }
        }
    }


    public View getTabView(int position) {
        View v = LayoutInflater.from(WABusiActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imagePress[position]);
        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WABusiActivity.this, 400, 110);

        if (position == 0) {
            img.setText(R.string.recent_status);
            img.setTextColor(ContextCompat.getColor(mContext, R.color.tab_unpress_text_color));
        } else {
            img.setText(R.string.help);
            img.setTextColor(ContextCompat.getColor(mContext, R.color.tab_unpress_text_color));
        }
        img.setLayoutParams(param);


        return v;
    }


    public View getTabViewUn(int position) {
        View v = LayoutInflater.from(WABusiActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imageUnPress[position]);

        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WABusiActivity.this, 400, 110);
        if (position == 0) {
            img.setText(R.string.recent_status);
            img.setTextColor(ContextCompat.getColor(mContext, R.color.tab_press_text_color));
        } else {
            img.setText(R.string.help);
            img.setTextColor(ContextCompat.getColor(mContext, R.color.tab_press_text_color));
        }

        img.setLayoutParams(param);


        return v;
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.waBusiBtn) {
            Intent localIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
            boolean installed = appInstalledOrNot("com.whatsapp.w4b");
            if (installed) {
                try {
                    startActivity(localIntent);
                } catch (Exception localActivityNotFoundException) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b")));
                }
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b")));
//                Toast.makeText(this, "WA Business not install in your device!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.backBtn) {
            onBackPressed();
        }
    }
}
