package com.techhive.statussaver;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.databinding.ActivityJoshBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.AsyncTaskExecutorService;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JoshActivity extends AppCompatActivity {

    String url;
    Context mContext;
    private Activity mActivity;
    HistoryRoomDatabase roomDatabase;
    ActivityJoshBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoshBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = mActivity= this;
        roomDatabase = HistoryRoomDatabase.getInstance(this);


        if (getIntent().getExtras() != null) {
            String sharedText = getIntent().getStringExtra("sharedText");
            binding.linkEdt.setText(sharedText);
        }


        Glide.with(JoshActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.josh_step_1))
                .into(binding.help1);

        Glide.with(JoshActivity.this)
                .load(R.drawable.josh_step_2)
                .into(binding.help2);

        Glide.with(JoshActivity.this)
                .load(R.drawable.josh_step_3)
                .into(binding.help3);

        Glide.with(JoshActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help4);


        binding.downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(JoshActivity.this)) {
                if (binding.linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(JoshActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    url = binding.linkEdt.getText().toString();
                    if (url.contains("myjosh")) {
                        History history = new History(System.nanoTime(), "Josh", url);
                        addHistory(history);
                        new callGetJoshData(url).execute();
                    } else {
                        Toast.makeText(JoshActivity.this, getResources().getString(R.string.url_not_exits), Toast.LENGTH_SHORT).show();
                    }

                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(JoshActivity.this, SharedPrefs.getStringValue(JoshActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                }
            } else {
                Toast.makeText(JoshActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        binding.imgJosh.setOnClickListener(v -> openJosh());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(JoshActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
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

    private void openJosh() {
        try {
            Intent i = this.getPackageManager().getLaunchIntentForPackage("com.eterno.shortvideos");
            this.startActivity(i);
        } catch (Exception e) {
            this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + "com.eterno.shortvideos")));
        }

    }


    public class callGetJoshData extends AsyncTaskExecutorService<String, Void, Document> {
        Document JoshDoc;
        String url;

        public callGetJoshData(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            Utils.displayLoader(JoshActivity.this);
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String s) {
            try {
                JoshDoc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return JoshDoc;
        }

        @Override
        protected void onPostExecute(Document document) {
            Utils.dismissLoader();
            try {
                String url = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!url.equals("")) {
                    JSONObject jsonObject = new JSONObject(url);
                    String VideoUrl = jsonObject.getJSONObject("props")
                            .getJSONObject("pageProps").getJSONObject("detail")
                            .getJSONObject("data").
                            getString("mp4_url");
//                    startDownload(VideoUrl, ROOTDIRECTORYJOSH, activity, "josh_" + System.currentTimeMillis() + ".mp4");
                    Utils.downloader(JoshActivity.this, VideoUrl, Utils.joshDirPath, "josh_" + System.currentTimeMillis() + ".mp4");
                    binding.linkEdt.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addHistory(History history) {
        AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> roomDatabase.historyDao().insert(history)));
    }
}
