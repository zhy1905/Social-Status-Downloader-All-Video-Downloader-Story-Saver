package com.techhive.statussaver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.adapter.FolderAdapter;
import com.techhive.statussaver.adapter.HomeAppsAdapter;
import com.techhive.statussaver.model.Apps;
import com.techhive.statussaver.model.Folder;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FolderFragment extends Fragment {

    private Context mContext;
    FolderAdapter folderAdapter;
    RecyclerView recFolders;
    private ArrayList<Apps> listOfApps = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        recFolders = view.findViewById(R.id.recFolders);

        getJson();

    }

    private void getJson() {
        String lang = SharedPrefs.getLanguage(mContext);
        if (lang.equals("")) {
            SharedPrefs.setLanguage(mContext, SharedPrefs.ENGLISH_LOCALE);
            lang = SharedPrefs.getLanguage(mContext);
        }

        listOfApps.clear();
        String jsonObjectString = Utils.getJsonFromAssets(mContext, "apps.json");
        if (jsonObjectString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonObjectString);
                JSONArray jsonArray = jsonObject.getJSONArray("apps");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject appObj = jsonArray.getJSONObject(i);
                    boolean isVisible = appObj.getBoolean("visible");
                    if (isVisible) {
                        Apps apps = new Apps();
                        apps.setId(appObj.getInt("id"));

                        String app_name = appObj.getString("app_name");
                        apps.setApp_name(app_name);

                        String name = appObj.getString("name");
                        String gujarati_name = appObj.getString("gujarati_name");
                        String hindi_name = appObj.getString("hindi_name");

                        switch (lang) {
                            case SharedPrefs.ENGLISH_LOCALE:
                                apps.setName(name);
                                break;
                            case SharedPrefs.GUJARATI_LOCALE:
                                apps.setName(gujarati_name);
                                break;
                            case SharedPrefs.HINDI_LOCALE:
                                apps.setName(hindi_name);
                                break;
                        }
                        listOfApps.add(apps);
                    }
                }
                recFolders.setLayoutManager(new GridLayoutManager(mContext, 3));
                folderAdapter = new FolderAdapter(mContext, listOfApps);
                recFolders.setAdapter(folderAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
