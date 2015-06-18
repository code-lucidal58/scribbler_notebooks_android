package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.CustomViews.LeftNavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.CustomViews.NotificationDrawer;
import com.scribblernotebooks.scribblernotebooks.Fragments.ClaimedDeals;
import com.scribblernotebooks.scribblernotebooks.Fragments.DealsFragment;
import com.scribblernotebooks.scribblernotebooks.Fragments.ManualScribblerCode;
import com.scribblernotebooks.scribblernotebooks.Fragments.ProfileFragment;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;
import com.scribblernotebooks.scribblernotebooks.Services.LocationRetreiver;
import com.scribblernotebooks.scribblernotebooks.Services.TokenRetriever;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


public class NavigationDrawer extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener,
        ManualScribblerCode.OnFragmentInteractionListener, ClaimedDeals.OnFragmentInteractionListener{


    OnNavKeyPressed keyListener;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String TAG="NavigationActivity";
    public DrawerLayout mDrawerLayout;
    public static GoogleApiClient mGoogleApiClient;
    public View mainView;
    View decorView;

    String url = "";
    Fragment fragment;
    static Context sContext;


    GoogleCloudMessaging gcm;
    AtomicInteger msg_id=new AtomicInteger();
    String regId;
    String SENDER_ID="872898499478";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext=getApplicationContext();

        //Retrieve token
        startService(new Intent(this, TokenRetriever.class));

        if(!checkPlayServices()){
            Toast.makeText(this, "Google play services not installed on your device. Notification won't be shown", Toast.LENGTH_LONG).show();
        }
        /** Save the whole view in a variable to pass into different modules of the app */
        mainView = View.inflate(getApplicationContext(), R.layout.activity_navigation_drawer, null);
        setContentView(mainView);


        /**Registering user with GCM service**/
        if(!checkPlayServices()){
            Toast.makeText(this,"Google play services not installed on your device. Notification won't be shown",Toast.LENGTH_LONG).show();
        }
        else{
            gcm=GoogleCloudMessaging.getInstance(getApplicationContext());
            regId=getRegistrationId(getApplicationContext());
//            Log.e("GCM","Registered "+regId);
            if(regId.isEmpty()){
                registerInBackground();
            }
            else{
                sendToServer(regId);
            }
        }

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
                }, 3000);
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
            Log.e("Url",url+"-------------------"+getApplication().getPackageName());

            if(url.contains(getApplication().getPackageName())){
                Uri uri=getIntent().getData();
//              market://details?id=com.scribblernotebooks.scribblernotebooks&id=4

                String dealCode=uri.getQueryParameter("dealId");
                Log.e("DealCode",dealCode);

                if(!dealCode.isEmpty()) {
                    ManualScribblerCode msc = new ManualScribblerCode();
                    Log.e("Calling","Calling Popup");
                    msc.getDealDetails(dealCode,this);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Scribbler code",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, LocationRetreiver.class));
//        MixpanelAPI mixpanelAPI=Constants.getMixPanelInstance(this);
        JSONObject props=new JSONObject();
        try {
            props.put("Location",getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE).getString(Constants.PREF_DATA_LOCATION,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        mixpanelAPI.track("User",props);


        /** Load Manual Code Input Fragment **/
        fragment = ManualScribblerCode.newInstance(NavigationDrawer.this, url);
        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        Boolean registered=false;
        if(keyListener!=null){
            if(keyListener.onBackKeyPressed()){
                Log.e("NavigationDrawer","BackKey Event Registered");
                registered=true;
            }
        }
        if(!registered) {
            Log.e("NavigationDrawer","BackKey Event Not Registered");
            if (mDrawerLayout.isDrawerOpen(findViewById(R.id.left_drawer_relative)) || mDrawerLayout.isDrawerOpen(findViewById(R.id.notification_drawer))) {
                mDrawerLayout.closeDrawers();
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                this.finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            mGoogleApiClient.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * SignOut from Google+ Account and facebook
     */
    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
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

    /**
     * Function to check if google play services is available on the phone
     * @return boolean indicating the availability GMS
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private String getRegistrationId(Context context){
        final SharedPreferences gcmSharedPref= getGCMPreferences(context);
        String regId=gcmSharedPref.getString(Constants.GCM_REG_ID,"");
//        Log.e("GCM","Pref "+regId);
        if(regId.isEmpty()){
            return "";
        }
        int registeredVersion=gcmSharedPref.getInt(Constants.GCM_APP_VERSION,Integer.MIN_VALUE);
        int currentVersion=getAppVersion(context);
        if(registeredVersion!=currentVersion){
            Log.i(TAG,"App version changed");
            return "";
        }
        return regId;
    }

    private SharedPreferences getGCMPreferences(Context context){
        return getSharedPreferences(Constants.PREF_GCM_NAME,MODE_PRIVATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            profileFragment.onActivityResult(requestCode, resultCode, data);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */

    private void registerInBackground(){
        new GCMRegistration().execute();
    }

    private class GCMRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String msg="";
            Context context=getApplicationContext();
            try{
                if(gcm==null){
                    gcm= GoogleCloudMessaging.getInstance(context);
                }
                regId=gcm.register(SENDER_ID);
//                Log.e("GCM","Registered "+regId);
                msg="Device registered "+regId;
                sendToServer(regId);
                showToast(msg);
                //TODO: send registration id to backend server


                storeRegistrationId(context, regId);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * Store user reg id on server
     */
    public void sendToServer(String gcmkey){

        User user=Constants.getUser(this);
        Log.e("User Email for GCM",user.getEmail());
        new LongOperation().execute(user.getEmail(), gcmkey);
    }

    public class LongOperation extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                HashMap<String, String> data=new HashMap<>();
                data.put("email",strings[0]);
                data.put("gcmkey",strings[1]);

                URL url=new URL(Constants.ServerUrls.insertGCM);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                OutputStream os=connection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(Constants.getPostDataString(data));
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response=reader.readLine();
                JSONObject object=new JSONObject(response);
                Boolean success=Boolean.parseBoolean(object.optString("success"));
                if(success){
                    Log.e("GCM","Gcm key sent "+strings[1]);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }






    public void showToast(String msg){
//        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.e("GCM", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.GCM_REG_ID, regId);
        editor.putInt(Constants.GCM_APP_VERSION, appVersion);
        editor.apply();
//        Log.e("GCM", "After save +" + prefs.getString(Constants.GCM_REG_ID, ""));
    }




    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    public void onFragmentInteraction() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUserNameChanged() {
        LeftNavigationDrawer.setUserName();

    }
    @Override
    public void onUserEmailChanged() {
        LeftNavigationDrawer.setUserEmail();
    }

    @Override
    public void onUserDPChanged() {
        LeftNavigationDrawer.setUserProfilePic();
    }

    @Override
    public void onUserCoverChanged() {
        LeftNavigationDrawer.setUserCover();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ;
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==android.R.id.home){
            if(getSupportFragmentManager().getBackStackEntryCount()>0){
                getSupportFragmentManager().popBackStack();
            }
        }
        return true;
    }


    public  interface OnNavKeyPressed{
        boolean onBackKeyPressed();
    }

    public void setKeyListener(OnNavKeyPressed onNavKeyPressed){
        keyListener=onNavKeyPressed;
    }



}
