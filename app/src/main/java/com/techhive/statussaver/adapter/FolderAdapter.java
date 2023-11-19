package com.techhive.statussaver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.MyGalleryActivity;
import com.techhive.statussaver.fragment.ChingariFragment;
import com.techhive.statussaver.fragment.DailymotionFragment;
import com.techhive.statussaver.fragment.FacebookFrag;
import com.techhive.statussaver.fragment.InstagramFragment;
import com.techhive.statussaver.fragment.JoshFragment;
import com.techhive.statussaver.fragment.PinterestFragment;
import com.techhive.statussaver.fragment.TrillerFragment;
import com.techhive.statussaver.fragment.VimeoFragment;
import com.techhive.statussaver.fragment.WABusFragment;
import com.techhive.statussaver.fragment.WAppFragment;
import com.techhive.statussaver.fragment.YoutubeFragment;
import com.techhive.statussaver.model.Apps;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    private final Context mContext;
    private final ArrayList<Apps> listOfFolder;

    public FolderAdapter(Context mContext, ArrayList<Apps> listOfFolder) {
        this.mContext = mContext;
        this.listOfFolder = listOfFolder;
    }

    @NonNull
    @Override
    public FolderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.MyViewHolder holder, int position) {
        Apps apps = listOfFolder.get(position);
        holder.txtName.setText(apps.getName());

        holder.folderMainLayout.setOnClickListener(view -> {
            if (apps.getApp_name().equalsIgnoreCase("Whatsapp")) {
                ((MyGalleryActivity) mContext).replaceFragment(new WAppFragment(), WAppFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("WA Business")) {
                ((MyGalleryActivity) mContext).replaceFragment(new WABusFragment(), WABusFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Instagram")) {
                ((MyGalleryActivity) mContext).replaceFragment(new InstagramFragment(), InstagramFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Josh")) {
                ((MyGalleryActivity) mContext).replaceFragment(new JoshFragment(), JoshFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Triller")) {
                ((MyGalleryActivity) mContext).replaceFragment(new TrillerFragment(), TrillerFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("facebook")) {
                ((MyGalleryActivity) mContext).replaceFragment(new FacebookFrag(), FacebookFrag.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Daily Motion")) {
                ((MyGalleryActivity) mContext).replaceFragment(new DailymotionFragment(), DailymotionFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Vimeo")) {
                ((MyGalleryActivity) mContext).replaceFragment(new VimeoFragment(), VimeoFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("Chingari")) {
                ((MyGalleryActivity) mContext).replaceFragment(new ChingariFragment(), ChingariFragment.class.getName());
            } else if (apps.getApp_name().equalsIgnoreCase("youtube")) {
                ((MyGalleryActivity) mContext).replaceFragment(new YoutubeFragment(), YoutubeFragment.class.getName());
            }else if (apps.getApp_name().equalsIgnoreCase("Pinterest")) {
                ((MyGalleryActivity) mContext).replaceFragment(new PinterestFragment(), PinterestFragment.class.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfFolder.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final LinearLayout folderMainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            folderMainLayout = itemView.findViewById(R.id.folderMainLayout);
        }
    }
}
