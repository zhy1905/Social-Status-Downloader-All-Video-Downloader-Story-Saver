package com.techhive.statussaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.InstaPref;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;
import com.techhive.statussaver.workers.AllVideoDownloadWorker;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.io.File;

public class FacebookActivity extends AppCompatActivity {
    ImageView backBtn;
    ImageView fbBtn;

    EditText linkEdt;
    TextView downloadBtn;
    ImageView help1, help2, help3, help4;
    RelativeLayout RLLoginInstagram;
    private SwitchCompat SwitchLogin;

    private Context mContext;

    private Activity mActivity;

    public static LifecycleOwner lifecycleOwner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        lifecycleOwner = this;
        mContext =mActivity= this;


        help1 = findViewById(R.id.help1);
        help2 = findViewById(R.id.help2);
        help3 = findViewById(R.id.help3);
        help4 = findViewById(R.id.help4);
        RLLoginInstagram = findViewById(R.id.RLLoginInstagram);
        SwitchLogin = findViewById(R.id.SwitchLogin);

        Glide.with(FacebookActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.fb_step_1))
                .into(help1);

        Glide.with(FacebookActivity.this)
                .load(R.drawable.fb_step_2)
                .into(help2);

        Glide.with(FacebookActivity.this)
                .load(R.drawable.fb_step_3)
                .into(help3);

        Glide.with(FacebookActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(help4);


        try {
            YoutubeDL.getInstance().init(this);
        } catch (YoutubeDLException e) {
            Log.e("TAG", "failed to initialize YouTube-android", e);
        }

        linkEdt = findViewById(R.id.linkEdt);
        downloadBtn = findViewById(R.id.downloadBtn);


        SwitchLogin.setChecked(InstaPref.getInstance(mContext).getBoolean(InstaPref.ISFBLOGIN));

        downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(FacebookActivity.this)) {
                if (linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(FacebookActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    final String url = linkEdt.getText().toString();

                    if (url.contains("facebook") || url.contains("fb")) {

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

                        linkEdt.setText("");
                    } else {
                        Toast.makeText(FacebookActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                    }

                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(FacebookActivity.this, SharedPrefs.getStringValue(FacebookActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                }
            } else {
                Toast.makeText(FacebookActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        fbBtn = findViewById(R.id.fbBtn);
        fbBtn.setOnClickListener(v -> launchFacebook());

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());
        if (!SharedPrefs.getIsPro(mContext)) {

            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                //admob
                AdManager.loadBannerAd(FacebookActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
                AdManager.loadNativeAds((Activity) mContext, frameLayout, SharedPrefs.getStringValue(mContext, SharedPrefs.nativeAdId));

            }

        }

        RLLoginInstagram.setOnClickListener(v -> {
            if (!InstaPref.getInstance(mContext).getBoolean(InstaPref.ISFBLOGIN)) {
                Intent intent = new Intent(mContext,
                        FBLoginActivity.class);
                activityResultLauncher.launch(intent);
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(mActivity);
                ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    InstaPref.getInstance(mActivity).putBoolean(InstaPref.ISFBLOGIN, false);
                    InstaPref.getInstance(mActivity).putString(InstaPref.FBKEY, "");
                    InstaPref.getInstance(mActivity).putString(InstaPref.FBCOOKIES, "");
                    SwitchLogin.setChecked(InstaPref.getInstance(mActivity).getBoolean(InstaPref.ISFBLOGIN));
                    dialog.cancel();

                });
                ab.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = ab.create();
                alert.setTitle(getResources().getString(R.string.do_u_want_to_download_media_from_pvt));
                alert.show();
            }

        });

        ImageView pasteBtn = findViewById(R.id.pasteBtn);
        pasteBtn.setOnClickListener(v -> {
            linkEdt.setText("");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                CharSequence text = clipboard.getPrimaryClip().getItemAt(0).getText();
                linkEdt.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        SwitchLogin.setChecked(InstaPref.getInstance(mContext).getBoolean(InstaPref.ISINSTALOGIN));
                    }
                }
            });

    private File getDownloadLocation() {
        File downloadsDir = Utils.downloadFBDir;
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

    public void launchFacebook() {
        String instagramApp = "com.facebook.katana";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(instagramApp);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.facebook_not_found, Toast.LENGTH_SHORT).show();
        }
    }


}

