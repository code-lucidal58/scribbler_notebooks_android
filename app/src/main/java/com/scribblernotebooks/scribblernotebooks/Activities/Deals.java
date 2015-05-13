package com.scribblernotebooks.scribblernotebooks.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.scribblernotebooks.scribblernotebooks.Adapters.SlidingPageAdapter;
import com.scribblernotebooks.scribblernotebooks.CustomViews.SlidingTabLayout;
import com.scribblernotebooks.scribblernotebooks.Fragments.CategoryList;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealList;
import com.scribblernotebooks.scribblernotebooks.R;


public class Deals extends AppCompatActivity implements CategoryList.OnFragmentInteractionListener, DealList.OnFragmentInteractionListener {

    SlidingPageAdapter slidingPageAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        // Set up the action bar.
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        slidingPageAdapter=new SlidingPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(slidingPageAdapter);
        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });

        SlidingTabLayout slidingTabLayout=(SlidingTabLayout)findViewById(R.id.slidingTab);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mViewPager);


//        for (int i = 0; i < slidingPageAdapter.getCount(); i++) {
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(slidingPageAdapter.getPageTitle(i))
//                            .setTabListener(this));
//        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//        mViewPager.setCurrentItem(tab.getPosition());
//
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//    }
}
