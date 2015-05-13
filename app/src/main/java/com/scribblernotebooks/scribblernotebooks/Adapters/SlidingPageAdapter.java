package com.scribblernotebooks.scribblernotebooks.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scribblernotebooks.scribblernotebooks.Fragments.CategoryList;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealList;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;

/**
 * Created by Jibin_ism on 08-May-15.
 */
public class SlidingPageAdapter extends FragmentStatePagerAdapter {

    String category="";


    public SlidingPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if(position==0)
            return new CategoryList();

        fragment=DealList.newInstance(category, getListType(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Category";
            case 1:
                return "All";
            case 2:
                return "New";
            case 3:
                return "Trending";
            default:
                return "";
        }
    }

    public String getListType(int position)
    {
        switch (position)
        {
            case 1:
                return Constants.LISTTYPE_ALL;
            case 2:
                return Constants.LISTTYPE_NEW;
            case 3:
                return Constants.LISTTYPE_TRENDING;
            default:
                return "";
        }
    }
}
