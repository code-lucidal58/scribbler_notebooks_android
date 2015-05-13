package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.scribblernotebooks.scribblernotebooks.CustomViews.DealPopup;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;


public class ManualScribblerCode extends ActionBarActivity {

    LinearLayout back;
    TextView textView;
    ImageView image;
    Button claimDeal;
    EditText code;
    String url="";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_scribbler_code);
        back=(LinearLayout)findViewById(R.id.backToScan);
        textView=(TextView)back.findViewById(R.id.textView);
        image=(ImageView)back.findViewById(R.id.imageView);
        claimDeal=(Button)findViewById(R.id.claimDeal);
        code=(EditText)findViewById(R.id.manualScribblerCodeInput);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        try{
            try {
                Intent i = getIntent();
                this.url = i.getStringExtra(Constants.EXTRA_DEAL_CODE);
                if (!url.isEmpty()) {
                    getDealDetailsAndShow(getDealDetails(Uri.parse(this.url)));
                }
            }catch (Exception e){
                Uri url=getIntent().getData();
                getDealDetailsAndShow(getDealDetails(url));
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        View.OnTouchListener backgroundChange=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setDrawable(back,getResources().getDrawable(R.drawable.scan_pressed));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_pressed));
                        break;
                    case MotionEvent.ACTION_UP:
                        setDrawable(back, getResources().getDrawable(R.drawable.scan_enabled));
                        startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
                        finish();
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
                url=code.getText().toString();
                getDealDetailsAndShow(url);
            }
        });

    }

    public void setDrawable(View v,Drawable drawable){
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.JELLY_BEAN){
            v.setBackgroundDrawable(drawable);
        }
        else{
            v.setBackground(drawable);
        }
    }

    public void getDealDetailsAndShow(String url){
        DealPopup dealPopup=new DealPopup(this);
        dealPopup.setUrl(url);
        dealPopup.show();
    }


    String getDealDetails(Uri url){
        String scheme,host,data,query;
        scheme=url.getScheme();
        host=url.getHost();
        data=url.getEncodedPath();
        query=url.getQuery();

        String dealCode,advertisersCode;
        dealCode=url.getQueryParameter("dealCode");
        advertisersCode=url.getQueryParameter("advertisersId");

        String s="Scheme: "+scheme+
                "\nHost: "+host+
                "\nData: "+data+
                "\nQuery: "+query+
                "\n\nDeal Code: "+dealCode+
                "\nAdvertisers Code: "+advertisersCode;

        return s;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_scribbler_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signOut) {

            SharedPreferences prefs=getSharedPreferences(Constants.USER_PREF,MODE_PRIVATE);
            SharedPreferences.Editor editor=prefs.edit();
            editor.putString(Constants.USER_NAME_PREF,Constants.NO_USER_SAVED);
            editor.apply();
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
                Toast.makeText(getApplicationContext(),"Logout successful",Toast.LENGTH_LONG).show();
            }
            startActivity(new Intent(this, LogIn.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
