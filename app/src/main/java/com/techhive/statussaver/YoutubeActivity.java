package com.techhive.statussaver;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.techhive.statussaver.databinding.ActivityYoutubeBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.Utils;
import com.techhive.statussaver.workers.AllVideoDownloadWorker;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.io.File;

public class YoutubeActivity extends AppCompatActivity {

    private Context mContext;

    Activity mActivity;

    public static LifecycleOwner lifecycleOwner;

    HistoryRoomDatabase roomDatabase;
    ActivityYoutubeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYoutubeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lifecycleOwner = this;
        mContext = mActivity=this;

        roomDatabase = HistoryRoomDatabase.getInstance(this);


        Glide.with(YoutubeActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.yt_step_1))
                .into(binding.help1);

        Glide.with(YoutubeActivity.this)
                .load(R.drawable.yt_step_2)
                .into(binding.help2);

        Glide.with(YoutubeActivity.this)
                .load(R.drawable.yt_step_3)
                .into(binding.help3);

        Glide.with(YoutubeActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help4);


        try {
            YoutubeDL.getInstance().init(this);
        } catch (YoutubeDLException e) {
            Log.e("TAG", "failed to initialize youtubedl-android", e);
        }

        binding.downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(YoutubeActivity.this)) {
                if (binding.linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(YoutubeActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    final String url = binding.linkEdt.getText().toString();

                    if (!url.contains("youtu")) {
                        Toast.makeText(YoutubeActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                    } else {

                        History history = new History(System.nanoTime(), "Youtube", url);
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

                }
            } else {
                Toast.makeText(YoutubeActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        binding.yTubeBtn.setOnClickListener(v -> launchYoutube());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

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
        File downloadsDir = Utils.downloadYTubeDir;
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

    public void launchYoutube() {
        String instagramApp = "com.google.android.youtube";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(instagramApp);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.youtube_not_found, Toast.LENGTH_SHORT).show();
        }
    }


    private void addHistory(History history) {
        AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> roomDatabase.historyDao().insert(history)));
    }
}

