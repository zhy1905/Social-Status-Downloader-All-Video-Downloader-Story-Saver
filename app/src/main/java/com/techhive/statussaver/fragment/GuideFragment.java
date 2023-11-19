package com.techhive.statussaver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.techhive.statussaver.R;
import com.techhive.statussaver.utils.AdManager;
import com.techhive.statussaver.utils.SharedPrefs;


public class GuideFragment extends Fragment {
    ImageView help1, help2, help3;
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View localView = inflater.inflate(R.layout.frag_wapp_guide, container, false);
        mContext = getActivity();
        help1 = localView.findViewById(R.id.help1);
        help2 = localView.findViewById(R.id.help2);
        help3 = localView.findViewById(R.id.help3);

        Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext, R.drawable.w_step_1))
                .into(help1);

        Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext, R.drawable.w_step_2))
                .into(help2);

        Glide.with(mContext)
                .load(ContextCompat.getDrawable(mContext, R.drawable.w_step_3))
                .into(help3);


        return localView;
    }
}
