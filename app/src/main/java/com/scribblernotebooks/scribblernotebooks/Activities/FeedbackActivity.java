package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

public class FeedbackActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText userEmail,feedback;
    RatingBar ratingBar;
    Button submit;
    SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        toolbar=(Toolbar)findViewById(R.id.appBar);
        userEmail=(EditText)findViewById(R.id.et_email);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        feedback=(EditText)findViewById(R.id.et_feedback);
        submit=(Button)findViewById(R.id.submit);

        Drawable progress=ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress,getResources().getColor(R.color.themeColor));

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Feedback");

        userPrefs=getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        userEmail.setText(Html.fromHtml("<b>"+userPrefs.getString(Constants.PREF_DATA_NAME,"")+"</b> &lt;"+userPrefs.getString(Constants.PREF_DATA_EMAIL,"")+"&gt;"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_feedback();
            }
        });
    }

    public void submit_feedback(){
        float rating=ratingBar.getRating();
        String feedback_message=feedback.getText().toString();

        //TODO: Send feedback to server
    }



}
