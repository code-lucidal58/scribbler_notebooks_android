package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.Adapters.NavigationListAdapter;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ImageLoader;
import com.scribblernotebooks.scribblernotebooks.R;


public class NavigationDrawer extends ActionBarActivity implements ProfileFragment.OnFragmentInteractionListener,
        DealsFragment.OnFragmentInteractionListener
{

    String[] mNavigationDrawerItemTitles;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    TextView uName;
    ImageView uPhoto;
    RelativeLayout mDrawer;
    NavigationListAdapter navigationListAdapter;
    Fragment fragment;
    String userName,userPhotoUrl ;
    ImageLoader imageLoader;
    SharedPreferences sharedPreferences;

    String url="";

    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        imageLoader=new ImageLoader(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer_relative);
        uName=(TextView)findViewById(R.id.userName);
        uPhoto=(ImageView)findViewById(R.id.userPhoto);

        sharedPreferences= getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        userName= sharedPreferences.getString(Constants.PREF_DATA_NAME, "UserName");
        userPhotoUrl=sharedPreferences.getString(Constants.PREF_DATA_PHOTO, "");


        uName.setText(userName);
        imageLoader.DisplayImage(userPhotoUrl, R.mipmap.ic_launcher, uPhoto);


        navigationListAdapter=new NavigationListAdapter(this,mNavigationDrawerItemTitles);
        mDrawerList.setAdapter(navigationListAdapter);


        try{
            url=getIntent().getData().toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        fragment=ManualScribblerCode.newInstance(NavigationDrawer.this,url);
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

            private void selectItem(int position) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
                        break;
                    case 1:
                        fragment=ManualScribblerCode.newInstance(NavigationDrawer.this,"");

                        break;
                    case 2:
                        fragment = new ProfileFragment();
                        break;
                    case 3:
                        fragment = DealsFragment.newInstance(Constants.serverURL);
                        break;
                    case 4:
                        fragment = DealsFragment.newInstance(Constants.serverURL+"featuredDeals");
                        break;
                    case 5:
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        signOut();
                        Toast.makeText(getBaseContext(), "Successfully Logged out", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getBaseContext(), LogIn.class));
                        finish();
                        break;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    try{
                        getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
                    }
                    catch (Exception e){
                        Log.e("Navigation Drawer","No Action Bar");
                    }
                    mDrawerLayout.closeDrawer(mDrawer);

                } else {
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }
        });
    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
}
