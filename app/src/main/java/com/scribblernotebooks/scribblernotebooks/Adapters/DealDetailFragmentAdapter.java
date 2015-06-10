package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scribblernotebooks.scribblernotebooks.Activities.DealDetail;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealDetailFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 09-Jun-15.
 */
public class DealDetailFragmentAdapter extends FragmentStatePagerAdapter {

    ArrayList<Deal> dealList;

    public DealDetailFragmentAdapter(FragmentManager fm, ArrayList<Deal> deals) {
        super(fm);
        dealList=deals;
    }

    @Override
    public Fragment getItem(int position) {
        return DealDetailFragment.newInstance(dealList.get(position));
    }

    @Override
    public int getCount() {
        return dealList.size();
    }
}
