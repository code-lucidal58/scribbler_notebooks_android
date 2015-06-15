package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Aanisha on 12-Jun-15.
 */
public class SignUpService extends AsyncTask<String, Void, User>
{
    Activity activity;
    String urlExtension;
    public SignUpService(Activity a,String s) {
        activity=a;
        urlExtension=s;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected User doInBackground(String... params) {
        Log.e("check","do in background");
        String name, email, mobile, password;
        name=params[0];
        email=params[1];
        mobile=params[2];
        password=params[3];
        HashMap<String,String> data=new HashMap<>();
        data.put("name",name);
        data.put("email",email);
        data.put("mobile",mobile);
        data.put("password",password);

        try {
            URL url = new URL(urlExtension);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(Constants.getPostDataString(data));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = br.readLine();
                if(urlExtension.equalsIgnoreCase(Constants.ServerUrls.login))
                {
                    User user = ParseJson.parseLoginResponse(response, activity);
                    if (user != null) {
                        return user;
                    }
                }
                else if(urlExtension.equalsIgnoreCase(Constants.ServerUrls.signUp)){
                    String[] token=ParseJson.parseSignupResponse(response,activity);
                    if (token != null) {
                        return new User(name,email,mobile,token[0],token[1]);
                    }
                }
            } else {
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        if (user == null) {
            return;
        }
        Constants.saveUserDetails(activity, user);
        activity.startActivity(new Intent(activity, NavigationDrawer.class));
        activity.finish();
        super.onPostExecute(user);
    }
}


