package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

public class MobileVerification extends AppCompatActivity {

    Toolbar toolbar;
    EditText otp;
    Button skip, okay, resend;
    TextView change;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        otp = (EditText) findViewById(R.id.otp);
        skip = (Button) findViewById(R.id.skip);
        okay = (Button) findViewById(R.id.okay);
        resend = (Button) findViewById(R.id.resend);
        change = (TextView) findViewById(R.id.changeNo);

        sharedPreferences=getSharedPreferences(Constants.PREF_ONE_TIME_NAME,MODE_PRIVATE);

        setSupportActionBar(toolbar);

        //user does not want to verify mobile no.
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getBaseContext(), NavigationDrawer.class));
                sharedPreferences.edit().putBoolean(Constants.PREF_SHOW_MOBILE,false).apply();
                finish();
            }
        });

        //user have entered OTP
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpNo=otp.getText().toString();
                if(!otpNo.isEmpty()){

                }else{
                    Toast.makeText(getBaseContext(),"Please enter the OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //resend OTP
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //user wants to change the registered mobile no.
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change.setTextColor(getResources().getColor(R.color.darkBlue));
                startActivity(new Intent(getBaseContext(),ProfileManagement.class));
                finish();
            }
        });
    }

    //to deactivate back press button
    @Override
    public void onBackPressed() {
    }
}
