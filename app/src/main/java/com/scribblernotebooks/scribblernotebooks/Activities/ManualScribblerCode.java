package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.CustomViews.DealPopup;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;


public class ManualScribblerCode extends Fragment {

    LinearLayout back;
    TextView textView;
    ImageView image;
    Button claimDeal;
    EditText code;
    String url = "";
    Uri uri;

    private Context mContext;
    private static Context sContext;

    private GoogleApiClient mGoogleApiClient;

    public static ManualScribblerCode newInstance(Context context,String url) {
        ManualScribblerCode fragment = new ManualScribblerCode();
        sContext=context;
        Bundle args = new Bundle();
        args.putString(Constants.URL_ARGUMENT, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        this.mContext=sContext;

        try {
            url = getArguments().getString(Constants.URL_ARGUMENT);
            if (!url.isEmpty() && !url.equals("")) {
                getDealDetailsAndShow(url);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //Initialize Google API code
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_manual_scribbler_code, container, false);


        back = (LinearLayout) v.findViewById(R.id.backToScan);
        textView = (TextView) back.findViewById(R.id.textView);
        image = (ImageView) back.findViewById(R.id.imageView);
        claimDeal = (Button) v.findViewById(R.id.claimDeal);
        code = (EditText) v.findViewById(R.id.manualScribblerCodeInput);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.PREF_DATA_NAME, "");



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
                        getActivity().finish();
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
                url = code.getText().toString();
                getDealDetailsAndShow(url);
            }
        });

//
        return v;
    }

    public void setDrawable(View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(drawable);
        } else {
            v.setBackground(drawable);
        }
    }

    public void getDealDetailsAndShow(String url) {
        DealPopup dealPopup = new DealPopup(mContext);
        dealPopup.setUrl(url);
        dealPopup.show();
    }


    String getDealDetails(Uri url) {
        String scheme, host, data, query;
        scheme = url.getScheme();
        host = url.getHost();
        data = url.getEncodedPath();
        query = url.getQuery();

        String dealCode, advertisersCode;
        dealCode = url.getQueryParameter("dealCode");
        advertisersCode = url.getQueryParameter("advertisersId");

        String s = "Scheme: " + scheme +
                "\nHost: " + host +
                "\nData: " + data +
                "\nQuery: " + query +
                "\n\nDeal Code: " + dealCode +
                "\nAdvertisers Code: " + advertisersCode;

        return s;

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_manual_scribbler_code, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.signOut) {
//
//            SharedPreferences prefs = getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString(Constants.USER_NAME_PREF, Constants.NO_USER_SAVED);
//            editor.apply();
//            try {
//                try {
//                    if (mGoogleApiClient.isConnected()) {
//                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                        mGoogleApiClient.disconnect();
//                        mGoogleApiClient.connect();
//                    }
//                }
//                catch (Exception e){
//                    LoginManager.getInstance().logOut();
//                }
//                Toast.makeText(getBaseContext(), "Successfully Logged out", Toast.LENGTH_LONG).show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            startActivity(new Intent(getBaseContext(), LogIn.class));
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
