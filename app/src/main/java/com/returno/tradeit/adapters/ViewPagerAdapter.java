package com.returno.tradeit.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.returno.tradeit.fragments.FavoritesFragment;
import com.returno.tradeit.fragments.FragmentMe;
import com.returno.tradeit.fragments.FragmentNotice;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int totalTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs=totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentMe();
            case 1:
                return new FavoritesFragment();
            case 2:
                return new FragmentNotice();
                default:return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
