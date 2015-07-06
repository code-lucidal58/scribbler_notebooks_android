package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

public class SplashScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        Thread timer=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    SharedPreferences sharedPreferences= getSharedPreferences(Constants.PREF_ONE_TIME_NAME,MODE_PRIVATE);
                    if(!sharedPreferences.getBoolean(Constants.PREF_SHOW_ILLUSTRATION,true)){
                        startActivity(new Intent(getApplicationContext(),LogIn.class));
                        finish();
                    }else{
                        startActivity(new Intent(getApplicationContext(), Illustrations.class));
                        finish();
                    }
                    overridePendingTransition(R.anim.login_slide_in, R.anim.profile_slide_out);
                }
            }
        });
        timer.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
