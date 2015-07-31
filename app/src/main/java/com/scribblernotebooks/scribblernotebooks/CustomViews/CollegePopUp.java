package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.scribblernotebooks.scribblernotebooks.BuildConfig;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.College;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aanisha on 25-Jun-15.
 * PopUp to get College from User
 */
public class CollegePopUp extends Dialog {

    Dialog dialog;
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView collegeName;
    Button skip, okay;
    ArrayList<String> college;
    ArrayList<College> colleges;
    SharedPreferences sharedPreferences;
    Context context;

    public CollegePopUp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removing title bar
        getCollegePopup();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        context=getContext();
        setContentView(R.layout.popup_college);
        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE);
        collegeName = (AutoCompleteTextView) findViewById(R.id.college);
        skip = (Button) findViewById(R.id.skip);
        okay = (Button) findViewById(R.id.okay);

        dialog = this;
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = collegeName.getText().toString();
                if (!text.isEmpty()) {

                    College c1=null;
                    for(College c:colleges){
                        if(c.getName().equalsIgnoreCase(text)){
                            c1=c;
                        }
                    }
                    User user = Constants.getUser(getContext());
                    user.setCollege(c1);
                    saveCollege(c1);
                    Constants.saveUserDetails(getContext(), user);
                    dialog.dismiss();
                }else{
                    collegeName.setError("Please enter your college name");
                }
            }
        });
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialog.dismiss();
        sharedPreferences.edit().putBoolean(Constants.PREF_SHOW_COLLEGE, false).apply();
    }

    public void saveCollege(College college){
        new AsyncTask<College, Void,Void>(){
            @Override
            protected Void doInBackground(College... params) {
                College college=params[0];
                User user=Constants.getUser(context);
                try {
                    URL url = new URL(Constants.ServerUrls.updateUser + user.getId());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setRequestProperty("Authorization", "Bearer " + user.getToken());

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constants.POST_COLLEGE, college.getId());
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String s=reader.readLine();

                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(college);
    }


    /**
     * Popup for College name
     */
    public void getCollegePopup() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url = new URL(Constants.ServerUrls.collegeList);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(5000);
                    connection.setDoInput(true);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    return reader.readLine();
                } catch (Exception e) {
                    Log.e("college popup", "do in background catch");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    colleges.clear();
                    Log.e("college popup", s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String success = jsonObject.optString(Constants.POST_SUCCESS);
                        if (Boolean.parseBoolean(success)) {
                            Log.e("college popup", "success");
                            JSONObject data = jsonObject.optJSONObject(Constants.POST_DATA);
                            Integer count = Integer.parseInt(data.optString(Constants.POST_COUNT));
                            JSONArray collegeList = data.optJSONArray(Constants.POST_COLLEGES);
                            for (int i = 0; i < count; i++) {
                                JSONObject c = collegeList.getJSONObject(i);
                                colleges.add(new College(c.optString("_id"), c.optString(Constants.POST_NAME)));
                                college.add(c.optString(Constants.POST_NAME));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("college popup", "postexecute catch");
                        e.printStackTrace();
                    }
                } else {
                    if(BuildConfig.DEBUG){
                        college.add("ISM");
                        college.add("IITB");
                        college.add("BITS");
                    }else{
                        return;
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, college);
                collegeName.setAdapter(arrayAdapter);

            }
        }.execute();
    }
}
