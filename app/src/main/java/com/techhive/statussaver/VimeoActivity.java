package com.techhive.statussaver;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.techhive.statussaver.databinding.ActivityVimeoBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;
import com.techhive.statussaver.workers.AllVideoDownloadWorker;

import java.io.File;

public class VimeoActivity extends AppCompatActivity {


    private Context mContext;

    Activity mActivity;

    public static LifecycleOwner lifecycleOwner;

    HistoryRoomDatabase roomDatabase;
    ActivityVimeoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVimeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lifecycleOwner = this;
        mContext = mActivity = this;

        roomDatabase = HistoryRoomDatabase.getInstance(this);

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.vimeoBtn.setOnClickListener(v -> openVimeo());

        binding.downloadBtn.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(VimeoActivity.this)) {
                final String URL = binding.linkEdt.getText().toString();
                if (URL.trim().length() == 0) {
                    Toast.makeText(VimeoActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    History history = new History(System.nanoTime(), "Vimeo", URL);
                    addHistory(history);

                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(VimeoActivity.this, SharedPrefs.getStringValue(VimeoActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }

                    File youtubeDLDir = getDownloadLocation();
                    Data arguments = new Data.Builder().putString(AllVideoDownloadWorker.DOWNLOAD_TYPE, "videoDownload")
                            .putString(AllVideoDownloadWorker.DIRECTORY, youtubeDLDir.getAbsolutePath())
                            .putString("VIDEO_URL", URL).build();


                    OneTimeWorkRequest downloaderWorkRequest = new OneTimeWorkRequest.Builder(AllVideoDownloadWorker.class)
                            .setInputData(arguments).build();
                    LiveData<WorkInfo> workInfoByIdLiveData = WorkManager.getInstance(mContext).getWorkInfoByIdLiveData(downloaderWorkRequest.getId());
                    workInfoByIdLiveData.observe(lifecycleOwner, workInfo -> {
                        if (workInfo.getState().equals(WorkInfo.State.RUNNING)) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.dl_started), Toast.LENGTH_SHORT).show();
                        }
                    });
                    WorkManager.getInstance(mContext).enqueue(downloaderWorkRequest);


                    binding.linkEdt.getText().clear();
                }
            } else {
                Toast.makeText(VimeoActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        Glide.with(VimeoActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.vimeo_1))
                .into(binding.help1);

        Glide.with(VimeoActivity.this)
                .load(R.drawable.vimeo_2)
                .into(binding.help2);

        Glide.with(VimeoActivity.this)
                .load(R.drawable.vimeo_3)
                .into(binding.help3);

        Glide.with(VimeoActivity.this)
                .load(R.drawable.vimeo_4)
                .into(binding.help4);

        Glide.with(VimeoActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help5);

        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(VimeoActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
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

    private void openVimeo() {
        try {
            Intent i = this.getPackageManager().getLaunchIntentForPackage("com.vimeo.android.videoapp");
            this.startActivity(i);
        } catch (Exception var4) {
            this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + "com.vimeo.android.videoapp")));
        }

    }

    private File getDownloadLocation() {
        File downloadsDir = Utils.downloadVimeoDir;
        if (!downloadsDir.exists()) {
            boolean isMake = downloadsDir.mkdir();
            if (isMake) Log.v("File Create ", " Success");
        }
        return downloadsDir;
    }

    private void addHistory(History history) {
        AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> roomDatabase.historyDao().insert(history)));
    }
}
