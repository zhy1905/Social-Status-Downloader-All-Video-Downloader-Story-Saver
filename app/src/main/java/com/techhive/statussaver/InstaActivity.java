package com.techhive.statussaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.databinding.ActivityInstaBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.InstaDownload;
import com.techhive.statussaver.utils.InstaPref;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

public class InstaActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    HistoryRoomDatabase roomDatabase;
    ActivityInstaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = mActivity= this;
        roomDatabase = HistoryRoomDatabase.getInstance(this);


        Glide.with(InstaActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.i_step_1))
                .into(binding.help1);

        Glide.with(InstaActivity.this)
                .load(R.drawable.i_step_2)
                .into(binding.help2);

        Glide.with(InstaActivity.this)
                .load(R.drawable.i_step_3)
                .into(binding.help3);

        Glide.with(InstaActivity.this)
                .load(ContextCompat.getDrawable(this, R.drawable.common_step_4))
                .into(binding.help4);


        binding.SwitchLogin.setChecked(InstaPref.getInstance(mContext).getBoolean(InstaPref.ISINSTALOGIN));


        binding.downloadBtn.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(InstaActivity.this)) {
                if (binding.linkEdt.getText().toString().trim().length() == 0) {
                    Toast.makeText(InstaActivity.this, getResources().getString(R.string.paste_url_and_download), Toast.LENGTH_SHORT).show();
                } else {
                    final String url = binding.linkEdt.getText().toString();

                    if (!Patterns.WEB_URL.matcher(url).matches() && !url.contains("instagram")) {
                        Toast.makeText(InstaActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                    } else {
                        History history = new History(System.nanoTime(), "Instagram", url);
                        addHistory(history);
                        String cookie = "ds_user_id=" + InstaPref.getInstance(mContext).getString(InstaPref.USERID)
                                + "; sessionid=" + InstaPref.getInstance(mContext).getString(InstaPref.SESSIONID);
                        if (Utils.isNullOrEmpty(cookie)) {
                            cookie = "";
                        }
//                            Log.e("CCCCCCCCC ",cookie);
                        InstaDownload.INSTANCE.startInstaDownload(url, InstaActivity.this, cookie);
                        binding.linkEdt.getText().clear();
                    }
                    if (!SharedPrefs.getIsPro(mContext)) {
                        if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow) && SharedPrefs.getBooleanValue(mContext, SharedPrefs.showInterstitial)) {
                            //ads
                            AdManager.adCounter++;
                            AdManager.showInterAd(InstaActivity.this, SharedPrefs.getStringValue(InstaActivity.this, SharedPrefs.interstitialAdId), null, 0);

                        }
                    }
                }
            } else {
                Toast.makeText(InstaActivity.this, getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
            }
        });


        binding.instaBtn.setOnClickListener(v -> launchInstagram());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
               //admob
                AdManager.loadBannerAd(InstaActivity.this, adContainer,SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
                AdManager.loadNativeAds((Activity) mContext, binding.flAdplaceholder, SharedPrefs.getStringValue(mContext, SharedPrefs.nativeAdId));

            }
        }

        binding.RLLoginInstagram.setOnClickListener(v -> {
            if (!InstaPref.getInstance(mContext).getBoolean(InstaPref.ISINSTALOGIN)) {
                Intent intent = new Intent(mContext,
                        LoginActivity.class);
                activityResultLauncher.launch(intent);
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    InstaPref.getInstance(mContext).putBoolean(InstaPref.ISINSTALOGIN, false);
                    InstaPref.getInstance(mContext).putString(InstaPref.COOKIES, "");
                    InstaPref.getInstance(mContext).putString(InstaPref.CSRF, "");
                    InstaPref.getInstance(mContext).putString(InstaPref.SESSIONID, "");
                    InstaPref.getInstance(mContext).putString(InstaPref.USERID, "");

                    binding.SwitchLogin.setChecked(InstaPref.getInstance(mContext).getBoolean(InstaPref.ISINSTALOGIN));
                    dialog.cancel();

                });
                ab.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = ab.create();
                alert.setTitle(getResources().getString(R.string.do_u_want_to_download_media_from_pvt));
                alert.show();
            }

        });
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        binding.SwitchLogin.setChecked(InstaPref.getInstance(mContext).getBoolean(InstaPref.ISINSTALOGIN));
                    }
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void launchInstagram() {
        String instagramApp = "com.instagram.android";
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(instagramApp);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.instagram_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void addHistory(History history) {
        AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> roomDatabase.historyDao().insert(history)));
    }


}
