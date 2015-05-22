package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.CustomViews.DealPopup;
import com.scribblernotebooks.scribblernotebooks.CustomViews.LeftNavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.CustomViews.NotificationDrawer;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ManualScribblerCode;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.SearchQueryFragment;
import com.scribblernotebooks.scribblernotebooks.R;

import java.util.Timer;
import java.util.TimerTask;


public class NavigationDrawer extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener,
        DealsFragment.OnFragmentInteractionListener,ManualScribblerCode.OnFragmentInteractionListener, SearchQueryFragment.OnFragmentInteractionListener {

    public DrawerLayout mDrawerLayout;
    public static GoogleApiClient mGoogleApiClient;
    View mainView;
    View decorView;
    String url = "";
    Fragment fragment;
    static Context sContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sContext=getApplicationContext();

        /** Save the whole view in a variable to pass into different modules of the app */
        mainView = View.inflate(getApplicationContext(), R.layout.activity_navigation_drawer, null);
        setContentView(mainView);

        /** Dimming Status Bar so that app is in focus */
        decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        NavigationDrawer.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                            }
                        });
                    }
                }, 1000);
            }
        });

        //View Setup
        mDrawerLayout = (DrawerLayout) mainView.findViewById(R.id.drawer_layout);

        /** Setting Up Navigation Drawer and Right Notification Drawer */
        setupNotificationDrawer();
        setupNavigationDrawer();

        /** Initializing Google+API incase user wants to logOut */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        /**Get Data from the external Barcode scanner or internal Barcode Scanner */
        try {
            url = getIntent().getData().toString();
            if(url.contains(getApplication().getPackageName())){
                getDealDetailsAndShow(url);
            }
            else{
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Load Manual Code Input Fragment **/
        fragment = ManualScribblerCode.newInstance(NavigationDrawer.this, url);
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    /**
     * SignOut from Google+ Account and facebook
     */
    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();

    }

    /**
     * Setup Notification Drawer. Defined in another class
     */
    void setupNotificationDrawer() {
        new NotificationDrawer(mainView, this);
    }

    /**
     * Setup Navigation Drawer. Defined in another class
     */
    void setupNavigationDrawer() {
        new LeftNavigationDrawer(this, mainView, this);
    }

    public void getDealDetailsAndShow(String url) {
        DealPopup dealPopup = new DealPopup(this);
        dealPopup.setUrl(url);
        dealPopup.show();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
