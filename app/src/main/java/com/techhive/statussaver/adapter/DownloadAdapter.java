package com.techhive.statussaver.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.techhive.statussaver.R;
import com.techhive.statussaver.PreviewActivity;
import com.techhive.statussaver.model.DataModel;
import com.techhive.statussaver.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private final Activity activity;
    File file;
    ArrayList<DataModel> listOfData;

    public DownloadAdapter(Activity activity, ArrayList<DataModel> listOfData) {
        this.activity = activity;
        this.listOfData = listOfData;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DataModel dataModel = listOfData.get(position);
        file = new File(dataModel.getFilePath());
        if (!file.isDirectory()) {
            if (!Utils.getBack(dataModel.getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
                try {
                    Glide.with(activity).load(file).apply(new RequestOptions().placeholder(R.color.black).error(android.R.color.black).optionalTransform(new RoundedCorners(1))).into(holder.imagevi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.imagePlayer.setVisibility(View.VISIBLE);
            } else if (!Utils.getBack(dataModel.getFilePath(), "((\\.3ga|\\.aac|\\.aif|\\.aifc|\\.aiff|\\.amr|\\.au|\\.aup|\\.caf|\\.flac|\\.gsm|\\.kar|\\.m4a|\\.m4p|\\.m4r|\\.mid|\\.midi|\\.mmf|\\.mp2|\\.mp3|\\.mpga|\\.ogg|\\.oma|\\.opus|\\.qcp|\\.ra|\\.ram|\\.wav|\\.wma|\\.xspf)$)").isEmpty()) {
                holder.imagePlayer.setVisibility(View.GONE);
            } else if (!Utils.getBack(dataModel.getFilePath(), "((\\.jpg|\\.png|\\.gif|\\.jpeg|\\.bmp)$)").isEmpty()) {
                holder.imagePlayer.setVisibility(View.GONE);
                Glide.with(activity).load(file).apply(new RequestOptions().placeholder(R.color.black).error(android.R.color.black).optionalTransform(new RoundedCorners(1))).into(holder.imagevi);
            }

            holder.deleteIV.setOnClickListener(v -> delete(position, activity));

            holder.shareIV.setOnClickListener(v -> {
//                Log.e("AAAAAAAAAA ",dataModel.getFilePath());
                share(dataModel.getFilePath(), activity);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOfData.size();
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        RelativeLayout cardView;
        private final ImageView imagePlayer;
        private final ImageView imagevi;
        private final ImageView shareIV;
        private final ImageView deleteIV;

        public ViewHolder(View itemView) {
            super(itemView);
            imagevi = itemView.findViewById(R.id.imageView);
            imagePlayer = itemView.findViewById(R.id.iconplayer);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            shareIV = itemView.findViewById(R.id.shareIV);
            deleteIV = itemView.findViewById(R.id.deleteIV);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity, PreviewActivity.class);
            intent.putParcelableArrayListExtra("images", listOfData);
            intent.putExtra("position", getAdapterPosition());
            intent.putExtra("statusdownload", "download");
            activity.startActivity(intent);


        }
    }

    void share(String path, Activity activity) {
        Utils.mShare(path, activity, path.contains(".mp4"));
    }

    void delete(final int position, Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getResources().getString(R.string.delete));
        alertDialog.setMessage(activity.getResources().getString(R.string.deleteConfirmation));
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            File file = new File(listOfData.get(position).getFilePath());
            if (file.exists()) {
                boolean isDel = file.delete();
                if (isDel) Log.v("Delete ", "Success");
                listOfData.remove(position);
                notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }
}
