package com.techhive.statussaver.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.adapter.WAppStatusAdapter;
import com.techhive.statussaver.model.DataModel;
import com.techhive.statussaver.utils.AsyncTaskExecutorService;
import com.techhive.statussaver.utils.SharedPrefs;
import com.techhive.statussaver.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class WAStatusFragment extends Fragment {

    ArrayList<DataModel> statusImageList = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    LinearLayout isEmptyList;
    WAppStatusAdapter mAdapter;
    TextView txt;

    ProgressBar loaderLay;
    LinearLayout sAccessBtn;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_wapp_status, container, false);
        mContext = getActivity();
        loaderLay = view.findViewById(R.id.loader);

        mRecyclerView = view.findViewById(R.id.my_recycler_view_0);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(this.mLayoutManager);
        isEmptyList = view.findViewById(R.id.isEmptyList);

        txt = view.findViewById(R.id.txt);

        sAccessBtn = view.findViewById(R.id.sAccessBtn);
        sAccessBtn.setOnClickListener(v -> {

            if (Utils.appInstalledOrNot(mContext, "com.whatsapp.w4b")) {
                StorageManager sm = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);

                Intent intent;
                String statusDir = getWhatsupFolder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                    Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

                    String scheme = uri.toString();

                    scheme = scheme.replace("/root/", "/document/");

                    scheme += "%3A" + statusDir;

                    uri = Uri.parse(scheme);

                    intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + statusDir));
                }


                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                activityResultLauncher.launch(intent);
            } else {
                Toast.makeText(getActivity(), mContext.getResources().getString(R.string.download_whatsapp_business), Toast.LENGTH_SHORT).show();
            }


        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!SharedPrefs.getWBTree(getActivity()).equals("")) {
            populateGrid();
        }
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        try {
                            requireContext().getContentResolver()
                                    .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        SharedPrefs.setWBTree(getActivity(), uri.toString());

                        populateGrid();
                    }
                }
            });

    loadDataAsync async;

    public void populateGrid() {

        async = new loadDataAsync();
        async.execute();
    }


    public class loadDataAsync extends AsyncTaskExecutorService<Void, Void, Void> {
        DocumentFile[] allFiles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderLay.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            sAccessBtn.setVisibility(View.GONE);
            isEmptyList.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void unused) {
            allFiles = null;
            statusImageList = new ArrayList<>();
            allFiles = getFromSdcard();
            if (allFiles != null) {
                for (DocumentFile allFile : allFiles) {
                    if (!allFile.getUri().toString().contains(".nomedia")) {
                        statusImageList.add(new DataModel(allFile.getUri().toString(),
                                allFile.getName()));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    Collections.reverse(statusImageList);
                    mAdapter = new WAppStatusAdapter(getActivity(), statusImageList, false);
                    mRecyclerView.setAdapter(mAdapter);
                    loaderLay.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                if (statusImageList == null || statusImageList.size() == 0) {
                    isEmptyList.setVisibility(View.VISIBLE);
                } else {
                    isEmptyList.setVisibility(View.GONE);
                }
            }, 300);
        }
    }

    private DocumentFile[] getFromSdcard() {
        String treeUri = SharedPrefs.getWBTree(getActivity());
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(requireContext().getApplicationContext(), Uri.parse(treeUri));
        if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory()
                && fromTreeUri.canRead() && fromTreeUri.canWrite()) {

            return fromTreeUri.listFiles();
        } else {
            return null;
        }
    }

    public String getWhatsupFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business" + File.separator + "Media" + File.separator + ".Statuses").isDirectory()) {
            return "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses";
        } else {
            return "WhatsApp Business%2FMedia%2F.Statuses";
        }
    }
}
