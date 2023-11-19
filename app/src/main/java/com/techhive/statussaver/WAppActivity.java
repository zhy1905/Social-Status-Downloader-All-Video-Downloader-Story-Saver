package com.techhive.statussaver;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.techhive.statussaver.adapter.MainPagerAdapter;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.LayoutManager;
import com.techhive.statussaver.utils.SharedPrefs;

import java.io.File;

public class WAppActivity extends AppCompatActivity implements View.OnClickListener {

    MainPagerAdapter adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    AdView mAdView;

    ImageView backBtn;
    ImageView wappBtn;
    Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wapp);
        mContext = mActivity = this;
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        String mBaseFolderPath = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.foldername) + File.separator;
        if (!new File(mBaseFolderPath).exists()) {
            boolean isMake = new File(mBaseFolderPath).mkdir();
            if (isMake) Log.v("File Create ", " Success");
        }

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);


        tabLayout = findViewById(R.id.sliding_tabs);
//        tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("OBJECT " + (position + 1))
        ).attach();


        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView(i));
        }

        View v = LayoutInflater.from(WAppActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imageUnPress[0]);
        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WAppActivity.this, 400, 110);
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

        wappBtn = findViewById(R.id.wappBtn);
        wappBtn.setOnClickListener(this);

        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(WAppActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));

            }
        }
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(WAppActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imagePress[position]);
        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WAppActivity.this, 400, 110);

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
        View v = LayoutInflater.from(WAppActivity.this).inflate(R.layout.custom_tab, null);
        TextView img = v.findViewById(R.id.imgView);
//        img.setBackgroundResource(imageUnPress[position]);
        LinearLayout.LayoutParams param;
        param = LayoutManager.setLinParams(WAppActivity.this, 400, 110);
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

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backBtn) {
            onBackPressed();
        } else if (id == R.id.wappBtn) {
            Intent localIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                try {
                    startActivity(localIntent);
                } catch (ActivityNotFoundException localActivityNotFoundException) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
                }
            } else {
                Toast.makeText(this, getString(R.string.whatsapp_not_available), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
