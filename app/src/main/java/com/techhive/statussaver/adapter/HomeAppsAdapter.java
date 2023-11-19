package com.techhive.statussaver.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.model.Apps;
import com.techhive.statussaver.utils.OnRecItemClickListener;

import java.util.ArrayList;

public class HomeAppsAdapter extends RecyclerView.Adapter<HomeAppsAdapter.MyViewHolder> {
    private final Context mContext;
    private final ArrayList<Apps> listOfApps;
    private OnRecItemClickListener itemClickListener;

    public HomeAppsAdapter(Context mContext, ArrayList<Apps> listOfApps) {
        this.mContext = mContext;
        this.listOfApps = listOfApps;
    }

    @NonNull
    @Override
    public HomeAppsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAppsAdapter.MyViewHolder holder, int position) {
        Apps apps = listOfApps.get(position);
        holder.txtName.setText(apps.getName());
        holder.cardMain.setCardBackgroundColor(Color.parseColor(apps.getBackgroundColor()));
        holder.imgIcon.setImageResource(apps.getIcon());

      /*  holder.AppsMainLayout.setOnClickListener(view -> {
            if (Apps.getName().equals(mContext.getResources().getString(R.string.whatsapp))) {
                ((MyGalleryActivity) mContext).replaceFragment(new WAppFragment(), WAppFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.wa_business))) {
                ((MyGalleryActivity) mContext).replaceFragment(new WABusFragment(), WABusFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.instagram))) {
                ((MyGalleryActivity) mContext).replaceFragment(new InstagramFragment(), InstagramFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.josh))) {
                ((MyGalleryActivity) mContext).replaceFragment(new JoshFragment(), JoshFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.triller))) {
                ((MyGalleryActivity) mContext).replaceFragment(new TrillerFragment(), TrillerFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.fb))) {
                ((MyGalleryActivity) mContext).replaceFragment(new FacebookFrag(), FacebookFrag.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.dailymotion))) {
                ((MyGalleryActivity) mContext).replaceFragment(new DailymotionFragment(), DailymotionFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.vimeo))) {
                ((MyGalleryActivity) mContext).replaceFragment(new VimeoFragment(), VimeoFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.chingari))) {
                ((MyGalleryActivity) mContext).replaceFragment(new ChingariFragment(), ChingariFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.youtube))) {
                ((MyGalleryActivity) mContext).replaceFragment(new YoutubeFragment(), YoutubeFragment.class.getName());
            } else if (Apps.getName().equals(mContext.getResources().getString(R.string.pinterest))) {
                ((MyGalleryActivity) mContext).replaceFragment(new PinterestFragment(), PinterestFragment.class.getName());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return listOfApps.size();
    }


    public void setClickListener(OnRecItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView txtName;
        private final CardView cardMain;
        private final ImageView imgIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            cardMain = itemView.findViewById(R.id.cardMain);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            cardMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}

