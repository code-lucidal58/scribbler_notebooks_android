package com.scribblernotebooks.scribblernotebooks.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        userPrefs=getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        userEmail.setText(Html.fromHtml("<b>"+userPrefs.getString(Constants.PREF_DATA_NAME,"")+"</b> &lt;"+userPrefs.getString(Constants.PREF_DATA_EMAIL,"")+"&gt;"));
        userEmail.setClickable(false);
        userEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_feedback();
            }
        });
    }

    public void submit_feedback(){
        final float rating=ratingBar.getRating();
        String feedback_message=feedback.getText().toString();
        String user=userEmail.getText().toString();
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params) {

                try{
                    URL url=new URL(Constants.ServerUrls.feedback);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(15000);
                    connection.setConnectTimeout(15000);

                    HashMap<String, String > data=new HashMap<>();
                    data.put("user",params[0]);
                    data.put("rating",params[1]);
                    data.put("message",params[2]);

                    OutputStream os=connection.getOutputStream();
                    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(user, String.valueOf(rating),feedback_message);
        //TODO: Send feedback to server
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_no_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
