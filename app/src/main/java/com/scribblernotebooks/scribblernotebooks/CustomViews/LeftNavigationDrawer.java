package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Activities.LogIn;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.NavigationListAdapter;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ManualScribblerCode;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 18-May-15.
 */
public class LeftNavigationDrawer {
    View mainView = null;
    Context mContext;

    String userName, userPhotoUrl;

    ListView mDrawerList;
    TextView uName;
    ImageView uPhoto;
    RelativeLayout mDrawer;
    NavigationListAdapter navigationListAdapter;
    SharedPreferences sharedPreferences;
    Fragment fragment;

    ArrayList<Pair<Integer,String>> mNavigationDrawerItems;
    NavigationDrawer navigationDrawerActivity;
    public DisplayImageOptions displayImageOptions;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;

    /**
     * Default constructor
     * @param navigationDrawerActivity the calling activity
     * @param activityView the root view
     * @param context context of application
     */
    public LeftNavigationDrawer(NavigationDrawer navigationDrawerActivity,View activityView, Context context) {
        super();
        this.navigationDrawerActivity=navigationDrawerActivity;
        this.mainView = activityView;
        this.mContext = context;

        /**Configurations for image caching library */
        imageLoaderConfiguration=new ImageLoaderConfiguration.Builder(this.mContext).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        imageLoadingListener=new SimpleImageLoadingListener();
        displayImageOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();


        instantiate();
    }

    /**
     * Initiating the views and data
     */
    protected void instantiate() {
        mDrawerList = (ListView) mainView.findViewById(R.id.left_drawer);
        mDrawer = (RelativeLayout) mainView.findViewById(R.id.left_drawer_relative);
        uName = (TextView) mainView.findViewById(R.id.userName);
        uPhoto = (ImageView) mainView.findViewById(R.id.userPhoto);


        /**Getting user details from shared preferences*/
        sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Constants.PREF_DATA_NAME, "UserName");
        userPhotoUrl = sharedPreferences.getString(Constants.PREF_DATA_PHOTO, "");

        uName.setText(userName);

        /**Loading and caching image from url*/
        ImageLoader.getInstance().displayImage(userPhotoUrl,uPhoto,displayImageOptions,imageLoadingListener);


        /**Setting navigation Drawer**/
        mNavigationDrawerItems=Constants.getNavigationDrawerItems();
        navigationListAdapter = new NavigationListAdapter(mContext, mNavigationDrawerItems);
        mDrawerList.setAdapter(navigationListAdapter);


        /**Handling clicks on navigation drawer**/
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

            private void selectItem(int position) {
                String title=mNavigationDrawerItems.get(position).second;
                switch (position) {
                    case 0:
                        mContext.startActivity(new Intent(mContext, ScannerActivity.class));
                        break;
                    case 1:
                        fragment = ManualScribblerCode.newInstance(mContext, "");

                        break;
                    case 2:
                        fragment = DealsFragment.newInstance(Constants.serverURL,title);
                        break;
                    case 3:
                        fragment = DealsFragment.newInstance(Constants.serverURL + "featuredDeals",title);
                        break;
                    case 4:
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
                        navigationDrawerActivity.getSupportActionBar().setTitle(mNavigationDrawerItems.get(position).second);
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
