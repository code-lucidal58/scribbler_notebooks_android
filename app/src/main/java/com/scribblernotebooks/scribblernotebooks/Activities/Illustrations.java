package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.scribblernotebooks.scribblernotebooks.Adapters.IllustrationsPageAdapter;
import com.scribblernotebooks.scribblernotebooks.R;

public class Illustrations extends AppCompatActivity {

    IllustrationsPageAdapter illustrationsPageAdapter;
    ViewPager mViewPager;
    String[] illusTitle,illusDesc;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getSharedPreferences("Illustrations",MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("firstTime",true)){
            startActivity(new Intent(this,LogIn.class));
            finish();
        }
        setContentView(R.layout.activity_illustrations);

        int[] imageId={R.drawable.illustration1,R.drawable.illustration2, R.drawable.illustration3};
        illusTitle=getResources().getStringArray(R.array.illustrationTitle);
        illusDesc=getResources().getStringArray(R.array.illustrationDesc);

        illustrationsPageAdapter = new IllustrationsPageAdapter(this,imageId,illusTitle,illusDesc);

        mViewPager = (ViewPager) findViewById(R.id.illustrations_pager);
        mViewPager.setAdapter(illustrationsPageAdapter);
    }

}
