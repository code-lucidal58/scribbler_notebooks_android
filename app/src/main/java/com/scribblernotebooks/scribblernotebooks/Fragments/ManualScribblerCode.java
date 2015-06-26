package com.scribblernotebooks.scribblernotebooks.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.Activities.MobileVerification;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ManualScribblerCode extends Fragment {


    User user;
    LinearLayout back;
    TextView textView;
    ImageView image;
    Button claimDeal, okay;
    EditText code;
    String url = "";
    Uri uri;
    LinearLayout ll;
    View instruct;
    //    ImageView notificationIcon;
    int screenWidth;
    int screenHeight;

    Deal currentDeal = null;
    RelativeLayout root;
    ShakeEventManager shakeEventManager;
    ImageView sun, cloud1, cloud2;

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

        this.mContext = sContext;

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
        sharedPreferences=mContext.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE);
        //to verify mobile
        if (sharedPreferences.getBoolean(Constants.PREF_SHOW_MOBILE, true)) {
            startActivity(new Intent(mContext, MobileVerification.class));
        }
        //Inflate view
        final View v = inflater.inflate(R.layout.fragment_manual_scribbler_code, container, false);
        CollegePopUp collegePopUp = new CollegePopUp(mContext);

        //View Setup
        if (sharedPreferences.getBoolean(Constants.PREF_SHOW_INSTRUCTIONS, true)) {
            instruct = v.findViewById(R.id.instruction);
            okay = (Button) instruct.findViewById(R.id.okay);
            instruct.setVisibility(View.VISIBLE);
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences.edit().putBoolean("showInstruct", false).apply();
                    instruct.setVisibility(View.GONE);
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
        back = (LinearLayout) v.findViewById(R.id.backToScan);
        textView = (TextView) back.findViewById(R.id.textView);
        image = (ImageView) back.findViewById(R.id.imageView);
        claimDeal = (Button) v.findViewById(R.id.claimDeal);
        code = (EditText) v.findViewById(R.id.manualScribblerCodeInput);
        ll = (LinearLayout) v.findViewById(R.id.ll);

        sun = (ImageView) v.findViewById(R.id.sun);
        cloud1 = (ImageView) v.findViewById(R.id.cloud1);
        cloud2 = (ImageView) v.findViewById(R.id.cloud2);

//        notificationIcon=(ImageView)v.findViewById(R.id.notificationIcon);

        final DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        //TODO: Uncomment with notification Drawer
//        final RelativeLayout mDrawer = (RelativeLayout) getActivity().findViewById(R.id.notification_drawer);
        /**
         * Opening Notification drawer on notification icon click
         */
//        notificationIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.openDrawer(mDrawer);
//            }
//        });


        /**
         * Setting up notification icon animation
         */
//        CyclicTransitionDrawable cyclicTransitionDrawable = new CyclicTransitionDrawable(new Drawable[]{
//                getResources().getDrawable(R.drawable.n1),
//                getResources().getDrawable(R.drawable.n2),
//                getResources().getDrawable(R.drawable.n3),
//                getResources().getDrawable(R.drawable.n4),
//                getResources().getDrawable(R.drawable.n5),
//                getResources().getDrawable(R.drawable.n6),
//                getResources().getDrawable(R.drawable.n7),
//                getResources().getDrawable(R.drawable.n8),
//        });
////        notificationIcon.setImageDrawable(cyclicTransitionDrawable);
//        cyclicTransitionDrawable.startTransition(NOTIFICATION_ICON_TRANSITION_DURATION, 0);
//        final Animation dance = AnimationUtils.loadAnimation(mContext, R.anim.dancing_notification_icon);
//        dance.setFillEnabled(true);
//        dance.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
////                notificationIcon.startAnimation(dance);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
////        notificationIcon.startAnimation(dance);


        /**
         * Setting Cloud motion animation
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Constants.setMovingAnimation(cloud1, Constants.getRandomInt(5000, 15000), screenWidth, (float) (Math.random() * (screenHeight / 2)) + 20, true, screenHeight);
        Constants.setMovingAnimation(cloud2, Constants.getRandomInt(7000, 20000), screenWidth, (float) (Math.random() * (screenHeight / 2)) + 20, true, screenHeight);


        /**Changing background of scan button programatically*/
        View.OnTouchListener backgroundChange = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_pressed));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_enabled));
                        startActivity(new Intent(mContext, ScannerActivity.class));
//                        Set<String> s=new HashSet<>();
//                        s.add("Male");
//                        s.add("Female");
//                        SurveyDialog surveyDialog=SurveyDialog.newInstance("123","What is your gender?",s);
//                        surveyDialog.show(getFragmentManager(),"SURVEY");
                        break;
                    default:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_enabled));
                        break;
                }
                return true;
            }
        };

        textView.setOnTouchListener(backgroundChange);
        image.setOnTouchListener(backgroundChange);
        back.setOnTouchListener(backgroundChange);

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
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
