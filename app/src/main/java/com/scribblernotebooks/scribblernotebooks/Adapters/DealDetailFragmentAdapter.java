package com.scribblernotebooks.scribblernotebooks.Adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.scribblernotebooks.scribblernotebooks.Fragments.DealDetailFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 09-Jun-15.
 */
public class DealDetailFragmentAdapter extends FragmentStatePagerAdapter {

    ArrayList<Deal> dealList;
    Boolean isClaimed;

    public DealDetailFragmentAdapter(FragmentManager fm, ArrayList<Deal> deals) {
        this(fm,deals,false);
    }

    public DealDetailFragmentAdapter(FragmentManager fm, ArrayList<Deal> deals, boolean isClaimed) {
        super(fm);
        dealList=deals;
        this.isClaimed=isClaimed;
    }

    @Override
    public Fragment getItem(int position) {
        return DealDetailFragment.newInstance(dealList.get(position), isClaimed);
    }

    @Override
    public int getCount() {
        return dealList.size();
    }
}
