package com.techhive.statussaver;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.databinding.ActivityPinterestBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.AsyncTaskExecutorService;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;
import com.techhive.statussaver.workers.AllVideoDownloadWorker;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class PinterestActivity extends AppCompatActivity {

    private Context mContext;

    Activity mActivity;

    public static LifecycleOwner lifecycleOwner;

    HistoryRoomDatabase roomDatabase;
    ActivityPinterestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinterestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lifecycleOwner = this;
        mContext = mActivity= this;

        roomDatabase = HistoryRoomDatabase.getInstance(this);


        Glide.with(PinterestActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.pin_step_1))
                .into(binding.help1);

        Glide.with(PinterestActivity.this)
                .load(R.drawable.pin_step_2)
                .into(binding.help2);

        Glide.with(PinterestActivity.this)
                .load(R.drawable.pin_step_3)
                .into(binding.help3);

        Glide.with(PinterestActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help4);


        try {
            YoutubeDL.getInstance().init(this);
        } catch (YoutubeDLException e) {
            Log.e("TAG", "failed to initialize youtubedl-android", e);
        }

        binding.downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(PinterestActivity.this)) {
                if (binding.linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(PinterestActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    final String url = binding.linkEdt.getText().toString();

                    if (!url.contains("pin")) {
                        Toast.makeText(PinterestActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                    } else {
                        History history = new History(System.nanoTime(), "Pinterest", url);
                        addHistory(history);

                        File youtubeDLDir = getDownloadLocation();
                        Data arguments = new Data.Builder().putString(AllVideoDownloadWorker.DOWNLOAD_TYPE, "videoDownload")
                                .putString(AllVideoDownloadWorker.DIRECTORY, youtubeDLDir.getAbsolutePath())
                                .putString("VIDEO_URL", url).build();


                        OneTimeWorkRequest downloaderWorkRequest = new OneTimeWorkRequest.Builder(AllVideoDownloadWorker.class)
                                .setInputData(arguments).build();
                        LiveData<WorkInfo> workInfoByIdLiveData = WorkManager.getInstance(mContext).getWorkInfoByIdLiveData(downloaderWorkRequest.getId());
                        workInfoByIdLiveData.observe(lifecycleOwner, workInfo -> {
                            if (workInfo.getState().equals(WorkInfo.State.RUNNING)) {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.dl_started), Toast.LENGTH_SHORT).show();
                            }
                        });
                        WorkManager.getInstance(mContext).enqueue(downloaderWorkRequest);

                        binding.linkEdt.setText("");
                    }
                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(PinterestActivity.this, SharedPrefs.getStringValue(PinterestActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                }
            } else {
                Toast.makeText(PinterestActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        binding.pinterestBtn.setOnClickListener(v -> launchPinterest());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(PinterestActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
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


    private File getDownloadLocation() {
        File downloadsDir = Utils.downloadPinterestDir;
        if (!downloadsDir.exists()) {
            boolean isMake = downloadsDir.mkdir();
            if (isMake) Log.v("File Create ", " Success");
        }
        return downloadsDir;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void launchPinterest() {
        String instagramApp = "com.pinterest";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(instagramApp);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.pinterest_not_found, Toast.LENGTH_SHORT).show();
        }
    }


    public class callGetPinData extends AsyncTaskExecutorService<String, Void, Document> {
        Document JoshDoc;
        String url;

        public callGetPinData(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            Utils.displayLoader(PinterestActivity.this);
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
//            Log.e("ddddddd ",document.toString());
            try {
                String url = document.select("script[data-test-id=\"video-snippet\"]").last().html();
                if (!url.equals("")) {
                    JSONObject jsonObject = new JSONObject(url);
                    Log.e("ddddddd ", jsonObject.toString());
                    String VideoUrl = jsonObject.
                            getString("contentUrl");
//                    startDownload(VideoUrl, ROOTDIRECTORYJOSH, activity, "josh_" + System.currentTimeMillis() + ".mp4");
                    Utils.downloader(PinterestActivity.this, VideoUrl, Utils.joshDirPath, "pin_" + System.currentTimeMillis() + ".mp4");
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


