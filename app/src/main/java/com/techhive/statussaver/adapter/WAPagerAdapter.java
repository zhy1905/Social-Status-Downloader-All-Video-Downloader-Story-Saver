package com.techhive.statussaver.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.techhive.statussaver.fragment.WAGuideFragment;
import com.techhive.statussaver.fragment.WAStatusFragment;

public class WAPagerAdapter extends FragmentStateAdapter {

    public WAPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new WAStatusFragment();
        } else {
            return new WAGuideFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
