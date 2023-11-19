package com.techhive.statussaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.viewpager.widget.ViewPager;

import com.techhive.statussaver.adapter.FullscreenImageAdapter;
import com.techhive.statussaver.model.DataModel;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.LayoutManager;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<DataModel> imageList;
    int position;


    LinearLayout menu_save, menu_share, menu_delete, linSave;
    FullscreenImageAdapter fullscreenImageAdapter;
    String statusdownload;


    ImageView backIV;
    LinearLayout bottomLay;
    TextView headerTxt;
    String folderPath;
    Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = mActivity = this;
        setContentView(R.layout.activity_preview);



        backIV = findViewById(R.id.backIV);

        bottomLay = findViewById(R.id.bottomLay);
        LinearLayout.LayoutParams botmparam = LayoutManager.setLinParams(PreviewActivity.this, 1080, 307);
        bottomLay.setLayoutParams(botmparam);

        viewPager = findViewById(R.id.viewPager);

        menu_save = findViewById(R.id.menu_save);
        linSave = findViewById(R.id.linSave);

        menu_share = findViewById(R.id.menu_share);

        menu_delete = findViewById(R.id.menu_delete);


        imageList = getIntent().getParcelableArrayListExtra("images");
        position = getIntent().getIntExtra("position", 0);
        statusdownload = getIntent().getStringExtra("statusdownload");
        folderPath = getIntent().getStringExtra("folderpath");

        if (statusdownload.equals("download")) {
            linSave.setVisibility(View.GONE);
        } else {
            linSave.setVisibility(View.VISIBLE);
        }

        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        viewPager.setAdapter(fullscreenImageAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!SharedPrefs.getIsPro(mContext)) {
                    if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                        //ads
                        AdManager.adCounter++;
                        AdManager.showInterAd(PreviewActivity.this, SharedPrefs.getStringValue(PreviewActivity.this, SharedPrefs.interstitialAdId), null, 0);

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        menu_save.setOnClickListener(clickListener);
        menu_share.setOnClickListener(clickListener);
        menu_delete.setOnClickListener(clickListener);
        backIV.setOnClickListener(clickListener);

        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(PreviewActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.backIV) {
                onBackPressed();
            } else if (id == R.id.menu_save) {
                if (imageList.size() > 0) {
                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(PreviewActivity.this, SharedPrefs.getStringValue(PreviewActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }

                    try {
                        Utils.download(PreviewActivity.this, imageList.get(viewPager.getCurrentItem()).getFilePath(), getIntent().getBooleanExtra("isWApp", false));
                        Toast.makeText(PreviewActivity.this, getResources().getString(R.string.status_save_success), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PreviewActivity.this, getResources().getString(R.string.sry_we_cant_move_this_file), Toast.LENGTH_LONG).show();
                    }
                } else {
                    finish();
                }
            } else if (id == R.id.menu_share) {
                if (imageList.size() > 0) {
                    Utils.shareFile(PreviewActivity.this, Utils.isVideoFile(PreviewActivity.this, imageList.get(viewPager.getCurrentItem()).getFilePath()), imageList.get(viewPager.getCurrentItem()).getFilePath());
                } else {
                    finish();
                }
            } else if (id == R.id.menu_delete) {
                if (imageList.size() > 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PreviewActivity.this);
                    alertDialog.setTitle(getString(R.string.delete));
                    alertDialog.setMessage(getString(R.string.deleteConfirmation));
                    alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        dialog.dismiss();
                        int currentItem = 0;

                        if (statusdownload.equals("download")) {
                            File file = new File(imageList.get(viewPager.getCurrentItem()).getFilePath());
                            if (file.exists()) {
                                boolean del = file.delete();
                                if (del) Log.e("delete ", "Success");
                                delete(currentItem);
                            }
                        } else {
                            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(PreviewActivity.this, Uri.parse(imageList.get(viewPager.getCurrentItem()).getFilePath()));
                            if (fromTreeUri != null) {
                                if (fromTreeUri.exists()) {
                                    boolean del = fromTreeUri.delete();
                                    if (del) Log.e("delete ", "Success");
                                    delete(currentItem);
                                }
                            }
                        }
                    });
                    alertDialog.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
                    alertDialog.show();
                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(PreviewActivity.this, SharedPrefs.getStringValue(PreviewActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                } else {
                    finish();
                }
            }
        }
    };

    void delete(int currentItem) {
        if (imageList.size() > 0 && viewPager.getCurrentItem() < imageList.size()) {
            currentItem = viewPager.getCurrentItem();
        }
        imageList.remove(viewPager.getCurrentItem());
        fullscreenImageAdapter = new FullscreenImageAdapter(PreviewActivity.this, imageList);
        viewPager.setAdapter(fullscreenImageAdapter);

        Intent intent = new Intent();
        setResult(10, intent);

        if (imageList.size() > 0) {
            viewPager.setCurrentItem(currentItem);
        } else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
