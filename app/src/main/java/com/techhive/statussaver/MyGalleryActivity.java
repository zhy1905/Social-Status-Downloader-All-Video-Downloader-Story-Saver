package com.techhive.statussaver;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.techhive.statussaver.fragment.FolderFragment;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.SharedPrefs;

public class MyGalleryActivity extends AppCompatActivity {

    ImageView backBtn;
    TextView headerTxt;
    public TextView txtToolbarName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        txtToolbarName = findViewById(R.id.txtToolbarName);
        txtToolbarName.setText(getResources().getString(R.string.my_gallery));

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());


        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, new FolderFragment(), FolderFragment.class.getName())
                .commit();

        if (!SharedPrefs.getIsPro(this)) {
            if (SharedPrefs.getBooleanValue(MyGalleryActivity.this, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);

                    //admob
                AdManager.loadBannerAd(MyGalleryActivity.this, adContainer, SharedPrefs.getStringValue(MyGalleryActivity.this, SharedPrefs.bannerAdId));
                AdManager.loadInterAd(MyGalleryActivity.this,SharedPrefs.getStringValue(MyGalleryActivity.this, SharedPrefs.interstitialAdId));

            }
        }

    }


    public void replaceFragment(Fragment fragment, String backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(backStack);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        txtToolbarName = findViewById(R.id.txtToolbarName);
        txtToolbarName.setText(getResources().getString(R.string.my_gallery));
    }
}
