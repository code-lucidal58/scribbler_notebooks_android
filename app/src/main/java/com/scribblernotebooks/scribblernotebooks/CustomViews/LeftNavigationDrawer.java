package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.LogIn;
import com.scribblernotebooks.scribblernotebooks.Fragments.ManualScribblerCode;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.NavigationListAdapter;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ImageLoader;
import com.scribblernotebooks.scribblernotebooks.R;

/**
 * Created by Jibin_ism on 18-May-15.
 */
public class LeftNavigationDrawer {
    View mainView = null;
    Context mContext;

    String userName, userPhotoUrl;
    ImageLoader imageLoader;

    ListView mDrawerList;
    TextView uName;
    ImageView uPhoto;
    RelativeLayout mDrawer;
    NavigationListAdapter navigationListAdapter;
    SharedPreferences sharedPreferences;
    Fragment fragment;

    String[] mNavigationDrawerItemTitles;
    NavigationDrawer navigationDrawerActivity;

    public LeftNavigationDrawer(NavigationDrawer navigationDrawerActivity,View activityView, Context context) {
        super();
        this.navigationDrawerActivity=navigationDrawerActivity;
        this.mainView = activityView;
        this.mContext = context;
        instantiate();
    }

    protected void instantiate() {
        mDrawerList = (ListView) mainView.findViewById(R.id.left_drawer);
        mDrawer = (RelativeLayout) mainView.findViewById(R.id.left_drawer_relative);
        uName = (TextView) mainView.findViewById(R.id.userName);
        uPhoto = (ImageView) mainView.findViewById(R.id.userPhoto);

        imageLoader = new ImageLoader(mContext);


        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Constants.PREF_DATA_NAME, "UserName");
        userPhotoUrl = sharedPreferences.getString(Constants.PREF_DATA_PHOTO, "");

        uName.setText(userName);
        imageLoader.DisplayImage(userPhotoUrl, R.mipmap.ic_launcher, uPhoto);


        mNavigationDrawerItemTitles = mContext.getResources().getStringArray(R.array.navigation_drawer_items_array);


        navigationListAdapter = new NavigationListAdapter(mContext, mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(navigationListAdapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

            private void selectItem(int position) {

                switch (position) {
                    case 0:
                        mContext.startActivity(new Intent(mContext, ScannerActivity.class));
                        break;
                    case 1:
                        fragment = ManualScribblerCode.newInstance(mContext, "");

                        break;
                    case 2:
                        fragment = new ProfileFragment();
                        break;
                    case 3:
                        fragment = DealsFragment.newInstance(Constants.serverURL);
                        break;
                    case 4:
                        fragment = DealsFragment.newInstance(Constants.serverURL + "featuredDeals");
                        break;
                    case 5:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        navigationDrawerActivity.signOut();
                        Toast.makeText(mContext, "Successfully Logged out", Toast.LENGTH_LONG).show();
                        mContext.startActivity(new Intent(mContext, LogIn.class));

                        break;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = navigationDrawerActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    try {
                        navigationDrawerActivity.getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
                    } catch (Exception e) {
                        Log.e("Navigation Drawer", "No Action Bar");
                    }
                    navigationDrawerActivity.mDrawerLayout.closeDrawer(mDrawer);

                } else {
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }
        });
    }




}
