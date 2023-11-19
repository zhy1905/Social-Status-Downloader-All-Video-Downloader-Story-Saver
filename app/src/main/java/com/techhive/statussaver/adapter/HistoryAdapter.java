package com.techhive.statussaver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.techhive.statussaver.R;
import com.techhive.statussaver.model.History;
import com.techhive.statussaver.utils.OnRecItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context mContext;
    private List<History> listOfHistories;
    private OnRecItemClickListener itemClickListener;

    public HistoryAdapter(Context mContext, List<History> listOfHistories) {
        this.mContext = mContext;
        this.listOfHistories = listOfHistories;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        History history = listOfHistories.get(position);
        holder.txtName.setText(history.getAppName());
        holder.txtUrl.setText(history.getUrl());
    }

    @Override
    public int getItemCount() {
        return listOfHistories.size();
    }

    public void setClickListener(OnRecItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtName, txtUrl;
        ImageView imgDelete;
        CardView cardHistory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtUrl = itemView.findViewById(R.id.txtUrl);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardHistory = itemView.findViewById(R.id.cardHistory);
            imgDelete.setOnClickListener(this);
            cardHistory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
