package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.scribblernotebooks.scribblernotebooks.Activities.LogIn;
import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.Activities.SettingsActivity;
import com.scribblernotebooks.scribblernotebooks.Adapters.NavigationRecyclerAdapter;
import com.scribblernotebooks.scribblernotebooks.Fragments.ClaimedDeals;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ManualScribblerCode;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 18-May-15.
 */
public class LeftNavigationDrawer {
    View mainView = null;
    Context mContext;
    static Context sContext;
    static View sMainView;

    String userName, userPhotoUrl, userCoverUrl;

    RecyclerView mDrawerList;
    TextView uName, userEmail;
    ImageView uPhoto,uCoverPic;
    RelativeLayout mDrawer;
    NavigationRecyclerAdapter navigationRecyclerAdapter;
    SharedPreferences sharedPreferences;
    Fragment fragment;
    FrameLayout userDetailsHolder;
    DrawerLayout mDrawerLayout;
    RelativeLayout settings, aboutUs, signOut;

    ArrayList<Pair<Integer,String>> mNavigationDrawerItems;
    NavigationDrawer navigationDrawerActivity;
    public DisplayImageOptions displayImageOptions;
    public DisplayImageOptions displayImageOptionsCover;
    public ImageLoadingListener imageLoadingListener;
    public ImageLoaderConfiguration imageLoaderConfiguration;
    public static DisplayImageOptions sdisplayImageOptions;
    public static DisplayImageOptions sdisplayImageOptionsCover;
    public static ImageLoadingListener simageLoadingListener;
    public static ImageLoaderConfiguration simageLoaderConfiguration;


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
        sContext=context;
        sMainView=activityView;

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
                .displayer(new SimpleBitmapDisplayer()).build();
        displayImageOptionsCover=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.navigation_drawer_cover_pic)
                .showImageForEmptyUri(R.drawable.navigation_drawer_cover_pic)
                .showImageOnFail(R.drawable.navigation_drawer_cover_pic)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();

        sdisplayImageOptions=displayImageOptions;
        sdisplayImageOptionsCover=displayImageOptionsCover;
        simageLoadingListener=imageLoadingListener;
        simageLoaderConfiguration=imageLoaderConfiguration;

        instantiate();
    }

    /**
     * Initiating the views and data
     */
    protected void instantiate() {
        mDrawerList = (RecyclerView) mainView.findViewById(R.id.left_drawer);
        mDrawer = (RelativeLayout) mainView.findViewById(R.id.left_drawer_relative);
        uName = (TextView) mainView.findViewById(R.id.userName);
        uPhoto = (ImageView) mainView.findViewById(R.id.userPhoto);
        uCoverPic = (ImageView) mainView.findViewById(R.id.userCover);
        userEmail = (TextView) mainView.findViewById(R.id.userEmail);
        userDetailsHolder = (FrameLayout) mainView.findViewById(R.id.userHolder);
        mDrawerLayout = (DrawerLayout) mainView.findViewById(R.id.drawer_layout);
        settings=(RelativeLayout)mainView.findViewById(R.id.settings);
        aboutUs=(RelativeLayout)mainView.findViewById(R.id.aboutUs);
        signOut=(RelativeLayout)mainView.findViewById(R.id.signout);

        /**
         * Open Profile management when user clicks section of navigation drawer
         */
        userDetailsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Fragment fragment = ProfileFragment.newInstance("Profile");
                FragmentManager fragmentManager = navigationDrawerActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        LeftNavigationDrawer.setUserName();
        LeftNavigationDrawer.setUserEmail();
        LeftNavigationDrawer.setUserCover();
        LeftNavigationDrawer.setUserProfilePic();


        /**Setting navigation Drawer**/
        mNavigationDrawerItems = Constants.getNavigationDrawerItems();
        navigationRecyclerAdapter = new NavigationRecyclerAdapter(mContext, mNavigationDrawerItems,this);
        mDrawerList.setLayoutManager(new LinearLayoutManager(mContext));
        mDrawerList.setAdapter(navigationRecyclerAdapter);

        /**Settings, Feedback and SignOut**/
        secondNavFunctions();
    }

        /**Handling clicks on navigation drawer**/
        public void clickAction(int position){
            String title = mNavigationDrawerItems.get(position).second;
            switch (position) {
                case 0:
                    mContext.startActivity(new Intent(mContext, ScannerActivity.class));
                    mDrawerLayout.closeDrawers();
                    break;
                case 1:
                    fragment = ManualScribblerCode.newInstance(mContext, "");
                    break;
                case 2:
                    fragment = DealsFragment.newInstance(Constants.serverURL, title);
                    break;
                case 3:
                    fragment = ClaimedDeals.newInstance();
                    break;
                default:
                    break;
            }

            if (fragment != null) {
                FragmentManager fragmentManager = navigationDrawerActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                try {
                    //noinspection ConstantConditions
                    navigationDrawerActivity.getSupportActionBar().setTitle(mNavigationDrawerItems.get(position).second);
                } catch (Exception e) {
                    Log.e("Navigation Drawer", "No Action Bar");
                }
                navigationDrawerActivity.mDrawerLayout.closeDrawer(mDrawer);

            } else {
                Log.e("MainActivity", "Error in creating fragment");
            }
        }

    /**
     * handling second part of navigation Drawer i.e. settings, feedback and signOut
     */
    public void secondNavFunctions()
    {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                navigationDrawerActivity.signOut();
                Toast.makeText(mContext, "Successfully Logged out", Toast.LENGTH_LONG).show();
                mContext.startActivity(new Intent(mContext, LogIn.class));
            }
        });
    }

    public static void setUserName(){
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        ((TextView)sMainView.findViewById(R.id.userName)).setText(sharedPreferences.getString(Constants.PREF_DATA_NAME, "Name"));
    }

    public static void setUserEmail(){
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        ((TextView)sMainView.findViewById(R.id.userEmail)).setText(sharedPreferences.getString(Constants.PREF_DATA_EMAIL, "user@domain.com"));
    }

    public static void setUserCover(){
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        /**Loading and caching image from url*/
        String coverUrl=sharedPreferences.getString(Constants.PREF_DATA_COVER_PIC, "");
        ImageView uCoverPic=(ImageView)sMainView.findViewById(R.id.userCover);
        if(!coverUrl.isEmpty()){
            if(coverUrl.contains("http") || coverUrl.contains("ftp")){
                ImageLoader.getInstance().displayImage(coverUrl,uCoverPic,sdisplayImageOptionsCover,simageLoadingListener);
            }else {
                uCoverPic.setImageBitmap(Constants.getScaledBitmap(coverUrl, 267, 200));
            }
        }
    }

    public static void setUserProfilePic(){
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        /**Loading and caching image from url*/
        String picUrl=sharedPreferences.getString(Constants.PREF_DATA_PHOTO, "");
        ImageView uPic=(ImageView)sMainView.findViewById(R.id.userPhoto);
        if(!picUrl.isEmpty()){
            if(picUrl.contains("http") || picUrl.contains("ftp")){
                ImageLoader.getInstance().displayImage(picUrl,uPic,sdisplayImageOptions,simageLoadingListener);
            }else {
                uPic.setImageBitmap(Constants.getScaledBitmap(picUrl, 267, 200));
            }
        }
    }





}
