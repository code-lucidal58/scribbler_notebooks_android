package com.scribblernotebooks.scribblernotebooks.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
    SharedPreferences sharedPreferences;

    public CollegePopUp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removing title bar
        getCollegePopup();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

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
                    User user = Constants.getUser(getContext());
                    user.setCollege(text);
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
                ArrayList<Pair<String, String>> col = new ArrayList<Pair<String, String>>();
                college = new ArrayList<String>();
                if (s != null) {
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
                                col.add(new Pair<String, String>(c.optString("_id"), c.optString(Constants.POST_NAME)));
                                college.add(c.optString(Constants.POST_NAME));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("college popup", "postexecute catch");
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
                college.add("ISM");
                college.add("IITB");
                college.add("BITS");

                arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, college);
                collegeName.setAdapter(arrayAdapter);

            }
        }.execute();
    }
}
