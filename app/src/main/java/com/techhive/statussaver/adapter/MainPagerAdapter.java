package com.techhive.statussaver.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.techhive.statussaver.fragment.GuideFragment;
import com.techhive.statussaver.fragment.WAppStatusFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new WAppStatusFragment();
        }  else {
            return new GuideFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
