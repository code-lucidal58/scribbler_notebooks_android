package com.scribblernotebooks.scribblernotebooks.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.scribblernotebooks.scribblernotebooks.Adapters.NavigationListAdapter;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;


public class NavigationDrawer extends ActionBarActivity implements ProfileFragment.OnFragmentInteractionListener,
        DealsFragment.OnFragmentInteractionListener
{

    String[] mNavigationDrawerItemTitles;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    RelativeLayout mDrawer;
    NavigationListAdapter navigationListAdapter;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer_relative);

        navigationListAdapter=new NavigationListAdapter(this,mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(navigationListAdapter);

        fragment = DealsFragment.newInstance(Constants.serverURL);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

            private void selectItem(int position) {

                switch (position) {
                    case 0:
                        fragment = new ProfileFragment();
                        break;
                    case 1:
                        fragment = DealsFragment.newInstance(Constants.serverURL);
                        break;
                    case 2:
                        fragment = DealsFragment.newInstance(Constants.serverURL+"featuredDeals");
                        break;

                    default:
                        break;
                }

                if (fragment != null) {
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
                    mDrawerLayout.closeDrawer(mDrawer);

                } else {
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {

    }
}
