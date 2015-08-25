package com.scribblernotebooks.scribblernotebooks.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.scribblernotebooks.scribblernotebooks.Adapters.DealDetailFragmentAdapter;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

public class DealDetail extends AppCompatActivity{

    DealDetailFragmentAdapter fragmentAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);

        Toolbar toolbar=(Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        ArrayList<Deal> dealArrayList=getIntent().getParcelableArrayListExtra(Constants.PARCELABLE_DEAL_LIST_KEY);
        int currentIndex=getIntent().getIntExtra(Constants.CURRENT_DEAL_INDEX,0);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        boolean isClaimed=getIntent().getBooleanExtra("IS_CLAIMED",false);
        fragmentAdapter = new DealDetailFragmentAdapter(getFragmentManager(),dealArrayList, isClaimed);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(currentIndex);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
