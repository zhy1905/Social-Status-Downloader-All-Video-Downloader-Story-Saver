package com.techhive.statussaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.techhive.statussaver.adapter.HistoryAdapter;
import com.techhive.statussaver.databinding.ActivityHistoryBinding;
import com.techhive.statussaver.roomdata.AppExecutors;
import com.techhive.statussaver.roomdata.HistoryRoomDatabase;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.OnRecItemClickListener;
import com.techhive.statussaver.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements OnRecItemClickListener {
    ActivityHistoryBinding binding;

    Context mContext;
    private Activity mActivity;
    HistoryRoomDatabase roomDatabase;
    List<History> listOfHistories = new ArrayList<>();
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = mActivity = this;
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        roomDatabase = HistoryRoomDatabase.getInstance(mContext);

        getHistory();

        binding.imgDelete.setOnClickListener(v -> {

            AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> {
                delete(-1);
            }));
        });

        binding.backBtn.setOnClickListener(v -> finish());

        if (!SharedPrefs.getIsPro(mContext)) {
            if (SharedPrefs.getBooleanValue(mContext, SharedPrefs.Adshow)) {
                LinearLayout adContainer = findViewById(R.id.banner_container);
                //admob
                AdManager.loadBannerAd(HistoryActivity.this, adContainer, SharedPrefs.getStringValue(mContext, SharedPrefs.bannerAdId));
            }

        }

    }

    private void getHistory() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<History> historyArrayList = roomDatabase.historyDao().getAllHistory();
            runOnUiThread(() -> {
                listOfHistories.clear();
                if (historyArrayList != null) listOfHistories.addAll(historyArrayList);
                if (listOfHistories.size() > 0) {
                    setupRecycle();
                } else {
                    binding.recHistory.setVisibility(View.GONE);
                    binding.isEmptyList.setVisibility(View.VISIBLE);
                }
            });
        });


    }

    private void setupRecycle() {
        binding.recHistory.setVisibility(View.VISIBLE);
        binding.isEmptyList.setVisibility(View.GONE);
        binding.recHistory.setLayoutManager(new LinearLayoutManager(mContext));
        historyAdapter = new HistoryAdapter(mContext, listOfHistories);
        binding.recHistory.setAdapter(historyAdapter);
        historyAdapter.setClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.imgDelete) {
            if (listOfHistories.size() > 0) {
                AppExecutors.getInstance().diskIO().execute(() -> runOnUiThread(() -> {
                    delete(position);
                }));
            }
        }
        if (id == R.id.cardHistory) {
            if (listOfHistories.size() > 0) {
                String url = listOfHistories.get(position).getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }
    }

    void delete(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(mContext.getResources().getString(R.string.delete));
        alertDialog.setMessage(mContext.getResources().getString(R.string.deleteConfirmation));
        alertDialog.setPositiveButton(mContext.getResources().getString(R.string.yes), (dialog, which) -> {
            if (listOfHistories.size() > 0) {
                if (position != -1) {
                    roomDatabase.historyDao().delete(listOfHistories.get(position));
                    listOfHistories.remove(position);
                } else {
                    roomDatabase.historyDao().deleteAll();
                    listOfHistories.clear();
                }
                historyAdapter.notifyDataSetChanged();
                if (listOfHistories.size() == 0) {
                    binding.recHistory.setVisibility(View.GONE);
                    binding.isEmptyList.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_hostory_found), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        alertDialog.setNegativeButton(mContext.getResources().getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }
}
