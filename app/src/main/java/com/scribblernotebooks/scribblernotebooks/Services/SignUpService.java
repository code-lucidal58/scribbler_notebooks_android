package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ProfileManagement;
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
public class SignUpService extends AsyncTask<HashMap<String, String>, Void, User> {
    String urlExtension;
    Activity activity;
    HashMap<String, String> parsedData;
    ProgressDialog progressDialog;

    public SignUpService(String s, Activity a) {
        urlExtension = s;
        activity = a;
        parsedData = new HashMap<>();
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Connecting");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @SafeVarargs
    @Override
    protected final User doInBackground(HashMap<String, String>... params) {

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
            writer.write(Constants.getPostDataString(params[0]));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = br.readLine();
                if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.login)) {
                    return loginHandle(response);
                } else if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.signUp)) {
                    return signupHandle(response, params[0]);
                } else if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.loginGoogle) || urlExtension.equalsIgnoreCase(Constants.ServerUrls.loginFacebook)) {
                    return loginSocialHandle(response, params[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        progressDialog.dismiss();
        if (user == null) {
            return;
        }
        Constants.saveUserDetails(activity, user);
        activity.startActivity(new Intent(activity, NavigationDrawer.class));
        activity.finish();
        super.onPostExecute(user);
    }

    public User loginHandle(String response) {
        parsedData = ParseJson.parseLoginResponse(response);
        if (parsedData != null) {
            if (Boolean.parseBoolean(parsedData.get(Constants.POST_SUCCESS))) {
                User user = new User(parsedData.get(Constants.POST_NAME), parsedData.get(Constants.POST_EMAIL),
                        parsedData.get(Constants.POST_MOBILE), parsedData.get(Constants.POST_TOKEN), parsedData.get(Constants.POST_MIXPANELID));
                activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PREF_DATA_PASS, "OK").apply();
                return user;
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Wrong username or password", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        return null;
    }

    public User signupHandle(String response, HashMap<String, String> params) {
        parsedData = ParseJson.parseSignupResponse(response);
        if (parsedData != null) {
            if (Boolean.parseBoolean(parsedData.get(Constants.POST_SUCCESS))) {
                activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PREF_DATA_PASS, "OK").apply();
                return new User(params.get(Constants.POST_NAME), params.get(Constants.POST_EMAIL),
                        params.get(Constants.POST_MOBILE), parsedData.get(Constants.POST_TOKEN), parsedData.get(Constants.POST_MIXPANELID));
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "User already exists", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        return null;
    }

    public User loginSocialHandle(String response, HashMap<String, String> params) {
        parsedData = ParseJson.parseLoginResponse(response);
        User user = null;
        if (parsedData != null) {
            //success is true
            if (Boolean.parseBoolean(parsedData.get(Constants.POST_SUCCESS))) {
                user = new User(parsedData.get(Constants.POST_NAME), parsedData.get(Constants.POST_EMAIL), parsedData.get(Constants.POST_MOBILE),
                        parsedData.get(Constants.POST_TOKEN), parsedData.get(Constants.POST_MIXPANELID));
            } else {
                saveUserDetails(params.get(Constants.POST_NAME), params.get(Constants.POST_EMAIL), params.get("profilePic")
                        , params.get("coverPic"), "", "", Constants.POST_GOOGLE, params.get(Constants.POST_GOOGLE));
            }
        }
        if (user != null) {
            user.setCoverImage(params.get("coverPic"));
            user.setProfilePic(params.get("profilePic"));
            return user;
        }
        return null;
    }

    private void saveUserDetails(String name, String userEmail, String userImageUrl, String userCoverPic, String mobileNo, String password, String idName, String id) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.PREF_DATA_NAME, name);
        editor.putString(Constants.PREF_DATA_PHOTO, userImageUrl);
        editor.putString(Constants.PREF_DATA_COVER_PIC, userCoverPic);
        editor.putString(Constants.PREF_DATA_EMAIL, userEmail);
        editor.putString(Constants.PREF_DATA_MOBILE, mobileNo);
        editor.putString(Constants.PREF_DATA_PASS, password);
        editor.apply();
        Intent i = new Intent(activity, ProfileManagement.class);
        i.putExtra(Constants.INTENT_ID_NAME, idName);
        i.putExtra(Constants.INTENT_ID_VALUE, id);
        activity.startActivity(i);
        activity.finish();
        activity.overridePendingTransition(R.anim.profile_slide_in, R.anim.login_slide_out);
        return;
    }
}


