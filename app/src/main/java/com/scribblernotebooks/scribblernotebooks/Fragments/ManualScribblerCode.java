package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.Activities.ScannerActivity;
import com.scribblernotebooks.scribblernotebooks.CustomViews.CollegePopUp;
import com.scribblernotebooks.scribblernotebooks.CustomViews.DealPopup;
import com.scribblernotebooks.scribblernotebooks.CustomViews.SurveyDialog;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Deal;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ShakeEventManager;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManualScribblerCode extends Fragment {


    User user;
    Button back;
    Button claimDeal, okay;
    EditText code;
    String url = "";
    Uri uri;
    LinearLayout ll;
    View instruct;
    //    ImageView notificationIcon;
    DrawerLayout drawerLayout;
    int screenWidth;
    int screenHeight;

    Deal currentDeal = null;
    RelativeLayout root;
    ShakeEventManager shakeEventManager;

    int NOTIFICATION_ICON_TRANSITION_DURATION = 1000;
    int notificationCount = 3;
    private Context mContext;
    private static Context sContext;
    private GoogleApiClient mGoogleApiClient;

    SharedPreferences sharedPreferences;
    /**
     * Statically initiating the Manual Code input fragment for the navigation drawer
     *
     * @param context the context of the activity
     * @param url     the url from the content of qr code
     * @return the fragment instance
     */
    public static ManualScribblerCode newInstance(Context context, String url) {
        ManualScribblerCode fragment = new ManualScribblerCode();
        sContext = context;
        Bundle args = new Bundle();
        args.putString(Constants.URL_ARGUMENT, url);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        user = Constants.getUser(mContext);


        //Initialize Google API code
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        //to verify mobile

        sharedPreferences=mContext.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE);
        this.mContext = getActivity();
//        if (sharedPreferences.getBoolean(Constants.PREF_SHOW_MOBILE, true)) {
//            startActivity(new Intent(mContext, MobileVerification.class));
//        }

        //Inflate view
        final View v = inflater.inflate(R.layout.fragment_manual_scribbler_code, container, false);
        CollegePopUp collegePopUp = new CollegePopUp(mContext);

        //View Setup
        if (sharedPreferences.getBoolean(Constants.PREF_SHOW_INSTRUCTIONS, true)) {
            drawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(GravityCompat.START);
            drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    sharedPreferences.edit().putBoolean(Constants.PREF_SHOW_INSTRUCTIONS,false).apply();
                }
            });
        }
        if (sharedPreferences.getBoolean(Constants.PREF_SHOW_COLLEGE, true)) {
            collegePopUp.show();
        }

        SharedPreferences surveyPref=mContext.getSharedPreferences(Constants.SURVEY_PREF_NAME, Context.MODE_PRIVATE);
        if(surveyPref.getBoolean(Constants.PREF_SURVEY_EXISTS,false)){
            SurveyDialog dialog=SurveyDialog.newInstance(surveyPref.getString(Constants.PREF_SURVEY_ID, ""),
                    surveyPref.getString(Constants.PREF_SURVEY_QUESTION, ""),
                    surveyPref.getStringSet(Constants.PREF_SURVEY_OPTIONS, null));
            dialog.show(getFragmentManager(), "SURVEY");
        }

        root = (RelativeLayout) v.findViewById(R.id.manualRoot);
        back = (Button) v.findViewById(R.id.backToScan);
        claimDeal = (Button) v.findViewById(R.id.claimDeal);
        code = (EditText) v.findViewById(R.id.manualScribblerCodeInput);
        ll = (LinearLayout) v.findViewById(R.id.ll);


        /**
         * Setting Cloud motion animation
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScannerActivity.class));
            }
        });

        claimDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show popup containing deal details
                url = code.getText().toString();
                if (!url.isEmpty()) {
                    getDealDetailsAndShow(url);
                }
            }
        });
        /**
         * Shake to refresh
         */
        try {
            shakeEventManager = new ShakeEventManager(sContext);
            shakeEventManager.setOnShakeListener(new ShakeEventManager.OnShakeListener() {
                @Override
                public void onShake() {
                    try {
                        Fragment fragment = DealsFragment.newInstance(Constants.serverURL, "Deals");
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    /**
     * Code for setting background of button taking into account the deprecation of default methods
     *
     * @param v        the view whose background is to be changed
     * @param drawable the background to be setup
     */
    public void setDrawable(View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(drawable);
        } else {
            v.setBackground(drawable);
        }
    }

    /**
     * Show the popup containing the deal details
     *
     * @param dealCode the url of the request
     */
    public void getDealDetailsAndShow(String dealCode) {
        getDealDetails(dealCode, mContext);
    }


    String response;

    /**
     * Under development Method
     * To decode data from the url query
     *
     * @param dealCode url to be decoded
     * @return the decoded strings
     */
    public void getDealDetails(final String dealCode, final Context mContext) {
        response = "";

        new AsyncTask<String, Void, String>() {

            ProgressDialog progressDialog = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Just a moment...\nWe are fetching your deal...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    //TODO:Modify url to send token and deal id
//                    URL url=Constants.getDealDetailsURL(params[0]);
                    user = Constants.getUser(mContext);
                    URL url = Constants.getDealDetailsURL(params[0], user.getToken());
                    Log.e("Url ", url.toString());
                    if (url == null)
                        return null;
                    Log.e("Deal Url", url.toString());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(15000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    return in.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if (s.isEmpty()) {
                    return;
                }
                DealPopup dealPopup = new DealPopup(mContext);
                Log.e("Deal", "Response " + s);

                Deal deal = ParseJson.parseSingleDealDetail(s);

                Log.e("Deals 2", deal.getId() + deal.getTitle() + deal.getCategory() + deal.getLongDescription() + deal.getShortDescription());
                dealPopup.setTitle(deal.getTitle());
                dealPopup.setCategory(deal.getCategory());
                dealPopup.setDescription(deal.getLongDescription());
                dealPopup.setImage(deal.getImageUrl());
                dealPopup.setCurrentDeal(deal);
                dealPopup.show();

                currentDeal = deal;
            }
        }.execute(dealCode);

    }

}
