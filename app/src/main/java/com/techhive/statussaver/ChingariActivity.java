package com.techhive.statussaver;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.databinding.ActivityChingriBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.AsyncTaskExecutorService;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ChingariActivity extends AppCompatActivity {

    String url = "";
    private ClipboardManager clipBoard;
    private Activity mActivity;
    Context mContext;
    ActivityChingriBinding binding;
    HistoryRoomDatabase roomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChingriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = mActivity = this;

        roomDatabase = HistoryRoomDatabase.getInstance(this);

        if (getIntent().getExtras() != null) {
            String sharedText = getIntent().getStringExtra("sharedText");
            binding.linkEdt.setText(sharedText);
        }

        Glide.with(ChingariActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.chingari_1))
                .into(binding.help1);

        Glide.with(ChingariActivity.this)
                .load(R.drawable.chingari_2)
                .into(binding.help2);

        Glide.with(ChingariActivity.this)
                .load(R.drawable.chingari_2)
                .into(binding.help3);

        Glide.with(ChingariActivity.this)
                .load(R.drawable.chingari_3)
                .into(binding.help4);

        Glide.with(ChingariActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help5);

        binding.downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(ChingariActivity.this)) {
                if (binding.linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(ChingariActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    url = binding.linkEdt.getText().toString();
                    if (url.contains("chingari")) {
                        History history = new History(System.nanoTime(), "Chingari", url);
                        addHistory(history);
                        new ChinAsync(url).execute();
                        binding.linkEdt.getText().clear();
                    } else {
                        Toast.makeText(ChingariActivity.this, getResources().getString(R.string.url_not_exits), Toast.LENGTH_SHORT).show();
                    }


                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(ChingariActivity.this, SharedPrefs.getStringValue(ChingariActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                }
            } else {
                Toast.makeText(ChingariActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        binding.chingBtn.setOnClickListener(v -> openChingari());

        binding.backBtn.setOnClickListener(v -> onBackPressed());


        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(ChingariActivity.this, adContainer,  SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
                AdManager.loadNativeAds((Activity) mContext, binding.flAdplaceholder, SharedPrefs.getStringValue(mContext, SharedPrefs.nativeAdId));

            }
        }


        binding.imgPaste.setOnClickListener(v -> {
            binding.linkEdt.setText("");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                CharSequence text = clipboard.getPrimaryClip().getItemAt(0).getText();
                binding.linkEdt.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mActivity = this;

    }


    private void openChingari() {
        String appName = "io.chingari.app";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(appName);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.instagram_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    public class ChinAsync extends AsyncTaskExecutorService<Void, Void, Document> {
        Document document;
        String urls;

        public ChinAsync(String urls) {
            this.urls = urls;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.displayLoader(ChingariActivity.this);
        }

        @Override
        protected Document doInBackground(Void unused) {
            try {
                document = Jsoup.connect(urls).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return document;
        }

        @Override
        protected void onPostExecute(Document result) {
            Utils.dismissLoader();
            try {
                url = document.select("meta[property=\"og:video:url\"]").last().attr("content");
                if (!url.equals("")) {

                    try {

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String file = "chingari" + "_" + timeStamp;
                        String ext = "mp4";
                        String fileName = file + "." + ext;

                        Utils.downloader(ChingariActivity.this, url, Utils.chinagriDirPath, fileName);

                        url = "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        binding.txtChingari.setText(mContext.getResources().getString(R.string.chingari));
    }

    private void addHistory(History history) {
        AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> roomDatabase.historyDao().insert(history)));
    }
}