package com.techhive.statussaver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.techhive.statussaver.R;
import com.techhive.statussaver.PreviewActivity;
import com.techhive.statussaver.model.DataModel;
import com.techhive.statussaver.utils.Utils;

import java.util.ArrayList;


public class WAppStatusAdapter extends RecyclerView.Adapter<WAppStatusAdapter.ViewHolder> {
    Context context;
    ArrayList<DataModel> mData;
    String folderPath;
    boolean isWApp;

    public WAppStatusAdapter(Context context, ArrayList<DataModel> dataModels, boolean isWApp) {
        this.mData = dataModels;
        this.context = context;
        this.isWApp = isWApp;
        folderPath = Utils.downloadWhatsAppDir.getAbsolutePath();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DataModel dataModel = this.mData.get(position);


        if (!Utils.getBack(dataModel.getFilePath(), "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)").isEmpty()) {
            holder.imagePlayer.setVisibility(View.VISIBLE);
        } else {
            holder.imagePlayer.setVisibility(View.GONE);
        }


        Glide.with(context).load(dataModel.getFilePath()).apply(new RequestOptions().placeholder(R.color.black).error(android.R.color.black).optionalTransform(new RoundedCorners(5))).into(holder.imagevi);


        holder.downloadIV.setOnClickListener(v -> {
            Utils.copyFileInSavedDir(context, dataModel.getFilePath(), isWApp);
            Toast.makeText(context, context.getResources().getString(R.string.saveSuccess), Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardView cardView;
        private final ImageView imagePlayer;
        private final ImageView imagevi;
        private final TextView downloadIV;

        public ViewHolder(View itemView) {
            super(itemView);
            imagevi = itemView.findViewById(R.id.imageView);
            imagePlayer = itemView.findViewById(R.id.iconplayer);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            downloadIV = itemView.findViewById(R.id.downloadIV);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putParcelableArrayListExtra("images", mData);
            intent.putExtra("position", getAdapterPosition());
            intent.putExtra("statusdownload", "status");
            intent.putExtra("isWApp", isWApp);
            intent.putExtra("folderpath", folderPath);
            context.startActivity(intent);
        }
    }


}
