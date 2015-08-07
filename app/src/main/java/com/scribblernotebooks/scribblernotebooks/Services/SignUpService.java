package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.NavigationDrawer;
import com.scribblernotebooks.scribblernotebooks.Activities.ProfileManagement;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.College;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.ParseJson;
import com.scribblernotebooks.scribblernotebooks.HelperClasses.User;
import com.scribblernotebooks.scribblernotebooks.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Aanisha on 12-Jun-15.
 */
public class SignUpService extends AsyncTask<HashMap<String, String>, Void, User> {
    String urlExtension;
    Activity activity;
    HashMap<String, String> parsedData;
    ProgressDialog progressDialog;
    Boolean isSignup = false;
    Boolean isLogin = false, isLoginSocial = false, fileNowFoundException = false;
    Boolean errorShown = false;
    Boolean success = false;

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

            if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.login)) {
                isLogin = true;
                try {
                    if (!params[0].get(Constants.POST_METHOD).isEmpty()) {
                        isLoginSocial = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(Constants.getPostDataString(params[0]));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = br.readLine();
            Log.e("Signup Service", "Response: " + response);
            if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.login)) {
                isSignup = false;
                return loginHandle(response);
            } else if (urlExtension.equalsIgnoreCase(Constants.ServerUrls.signUp)) {
                isSignup = true;
                return signupHandle(response, params[0]);
            }
        } catch (FileNotFoundException f) {
            if (isLogin && !isLoginSocial) {
                fileNowFoundException = true;
                return null;
            } else if (isLoginSocial) {
                activity.startActivity(new Intent(activity, ProfileManagement.class));
                activity.finish();
            }
        } catch (SocketTimeoutException s) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        progressDialog.dismiss();
        if (fileNowFoundException) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Error")
                    .setMessage("The entered Email-password combination is incorrect. Please try again")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            errorShown = true;
        }
        if (user == null && !isLoginSocial && !errorShown) {
            Toast.makeText(activity, "Unable to connect to server. Please try again", Toast.LENGTH_LONG).show();
            return;
        }
        if (user == null) {
            return;
        }

        activity.startService(new Intent(activity, ClaimedDealsRetriever.class));
        Constants.saveUserDetails(activity, user);
        User user1 = Constants.getUser(activity);
        if (user1.getMobile().isEmpty()) {
            activity.startActivity(new Intent(activity, ProfileManagement.class));
        } else {
            if (success)
                activity.startActivity(new Intent(activity, NavigationDrawer.class));
            else
                Toast.makeText(activity, "Unable to connect to server. Please try again", Toast.LENGTH_LONG).show();
        }
        activity.finish();
        super.onPostExecute(user);
    }

    public User loginHandle(String response) {
        parsedData = ParseJson.parseLoginResponse(response);
        if (parsedData != null) {
            if (Boolean.parseBoolean(parsedData.get(Constants.POST_SUCCESS))) {
                User user = new User(
                        parsedData.get(Constants.PREF_DATA_ID),
                        parsedData.get(Constants.POST_NAME),
                        parsedData.get(Constants.POST_EMAIL),
                        parsedData.get(Constants.POST_MOBILE),
                        parsedData.get(Constants.POST_COVERPIC),
                        parsedData.get(Constants.POST_PROFILEPIC),
                        parsedData.get(Constants.POST_TOKEN),
                        parsedData.get(Constants.POST_MIXPANELID));

                try {
                    user.setCollege(new College(parsedData.get(Constants.PREF_DATA_COLLEGE_ID), parsedData.get(Constants.PREF_DATA_COLLEGE_NAME)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (user.getCollege() == null) {
                    activity.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE).edit().putBoolean(Constants.PREF_SHOW_COLLEGE, true).apply();
                }

                activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PREF_DATA_PASS, "OK").apply();
                success = true;

                SharedPreferences.Editor sharedPreferences = activity.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferences.putBoolean(Constants.PREF_SHOW_COLLEGE, false);
                sharedPreferences.putBoolean(Constants.PREF_SHOW_MOBILE, false);
                sharedPreferences.apply();

                return user;
            }
        }
        return null;
    }

    public User signupHandle(String response, HashMap<String, String> params) {
        parsedData = ParseJson.parseSignupResponse(response);
        if (parsedData != null) {
            if (Boolean.parseBoolean(parsedData.get(Constants.POST_SUCCESS))) {
                activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Constants.PREF_DATA_PASS, "OK").apply();
                activity.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE).edit()
                        .putString(Constants.PREF_MOBILE_VERIFY_CODE, parsedData.get(Constants.POST_MOBILE_VERIFY)).apply();
                String cover, profilePic;
                Log.e("normal signIn", "signupHandle");
                try {
                    cover = params.get(Constants.POST_COVERPIC);
                } catch (Exception e) {
                    cover = "";
                }
                try {
                    profilePic = params.get(Constants.POST_PROFILEPIC);
                } catch (Exception e) {
                    profilePic = "";
                }
                success = true;
                SharedPreferences.Editor sharedPreferences = activity.getSharedPreferences(Constants.PREF_ONE_TIME_NAME, Context.MODE_PRIVATE).edit();
                sharedPreferences.putBoolean(Constants.PREF_SHOW_COLLEGE, true);
                sharedPreferences.putBoolean(Constants.PREF_SHOW_MOBILE, true);
                sharedPreferences.apply();
                return new User(params.get(Constants.PREF_DATA_ID),
                        params.get(Constants.POST_NAME),
                        params.get(Constants.POST_EMAIL),
                        params.get(Constants.POST_MOBILE),
                        cover, profilePic,
                        parsedData.get(Constants.POST_TOKEN),
                        parsedData.get(Constants.POST_MIXPANELID));
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
                user = new User(parsedData.get(Constants.PREF_DATA_ID),
                        parsedData.get(Constants.POST_NAME),
                        parsedData.get(Constants.POST_EMAIL),
                        parsedData.get(Constants.POST_MOBILE),
                        parsedData.get(Constants.POST_COVERPIC),
                        parsedData.get(Constants.POST_PROFILEPIC),
                        parsedData.get(Constants.POST_TOKEN),
                        parsedData.get(Constants.POST_MIXPANELID));
            } else {
                saveUserDetails(params.get(Constants.POST_NAME), params.get(Constants.POST_EMAIL), params.get("profilePic")
                        , params.get("coverPic"), "", "", Constants.POST_GOOGLE, params.get(Constants.POST_GOOGLE));
            }
        }
        if (user != null) {
            user.setCoverImage(params.get(Constants.POST_COVERPIC));
            user.setProfilePic(params.get(Constants.POST_PROFILEPIC));
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
    }
}


