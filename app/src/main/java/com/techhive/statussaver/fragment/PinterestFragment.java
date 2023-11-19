package com.techhive.statussaver.fragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.MyGalleryActivity;
import com.techhive.statussaver.adapter.DownloadAdapter;
import com.techhive.statussaver.model.DataModel;
import com.techhive.statussaver.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PinterestFragment extends Fragment {

    File file;
    ArrayList<DataModel> listOfImages = new ArrayList<>();
    ArrayList<DataModel> listOfVideos = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    LinearLayout isEmptyList;
    DownloadAdapter mAdapter;
    TextView txt;

    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = paramLayoutInflater.inflate(R.layout.fragment_download, paramViewGroup, false);
        mRecyclerView = view.findViewById(R.id.my_recycler_view_1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        isEmptyList = view.findViewById(R.id.isEmptyList);
        txt = view.findViewById(R.id.txt);

        ((MyGalleryActivity) requireActivity()).txtToolbarName.setText(getResources().getString(R.string.pinterest));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMedia();
    }

    public void loadMedia() {

        file = Utils.downloadPinterestDir;

        listOfImages.clear();
        listOfVideos.clear();
        if (!file.isDirectory()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Utils.checkPermissions(getActivity(), Utils.storage_permissions_33)) {
                displayfiles(file, mRecyclerView);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                displayfiles(file, mRecyclerView);
            }
        }
    }

    void displayfiles(File file, final RecyclerView mRecyclerView) {
        File[] listfilemedia = dirListByAscendingDate(file);
        if (listfilemedia != null) {
            if (listfilemedia.length != 0) {
                isEmptyList.setVisibility(View.GONE);
            } else {
                isEmptyList.setVisibility(View.VISIBLE);
            }
            int i = 0;
            while (i < listfilemedia.length) {
                listOfImages.add(new DataModel(listfilemedia[i].getAbsolutePath(), listfilemedia[i].getName()));
                i++;
            }
        }
        if (listOfImages.size() > 0) {
            isEmptyList.setVisibility(View.GONE);
        } else {
            isEmptyList.setVisibility(View.VISIBLE);
        }
        mAdapter = new DownloadAdapter(getActivity(), listOfImages);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public static File[] dirListByAscendingDate(File folder) {
        if (!folder.isDirectory()) {
            return null;
        }
        File[] sortedByDate = folder.listFiles();
        if (sortedByDate == null || sortedByDate.length <= 1) {
            return sortedByDate;
        }
        Arrays.sort(sortedByDate, Comparator.comparingLong(File::lastModified).reversed());
        return sortedByDate;
    }
}
