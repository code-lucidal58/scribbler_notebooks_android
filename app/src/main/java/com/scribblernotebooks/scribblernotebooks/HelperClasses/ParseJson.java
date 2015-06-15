package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.scribblernotebooks.scribblernotebooks.Activities.LogIn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Aanisha on 11-May-15.
 */

public class ParseJson {

    public static ArrayList<Deal> getParsedData(String url, Context context, Boolean b) {
        ArrayList<Deal> dealsList = new ArrayList<>();
        try {
            JSONObject jsonChild = new JSONObject(url);
            String success = jsonChild.optString("success");
            if (success.equalsIgnoreCase("false")) {
                return null;
            }
            JSONArray jsonArray = jsonChild.optJSONArray("message");

            int lengthJsonArr = jsonArray.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                Deal deal = parseSingleDeal(jsonChildNode.toString());
                dealsList.add(deal);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dealsList;
    }


    public static Deal parseSingleDeal(String jsonResponse) {
        Deal deal = new Deal();
//        Log.e("JSON","jsonResponse "+jsonResponse);
        try {
            JSONObject jsonChild = new JSONObject(jsonResponse);
            String success = jsonChild.optString("success");

            String id, title, category, sdesp, ldesp, imgurl;

            id = jsonChild.optString(Constants.TAG_ID);
            title = jsonChild.optString(Constants.TAG_DEAL_NAME);
            category = jsonChild.optString(Constants.TAG_CATEGORY);
            sdesp = jsonChild.optString(Constants.TAG_SHORT_DESCRIPTION);
            ldesp = jsonChild.optString(Constants.TAG_LONG_DESCRIPTION);
            // Image url http://www.ucarecdn.com/<image UUID>/image.png
            imgurl = "http://www.ucarecdn.com/" + jsonChild.optString(Constants.TAG_IMAGE_UUID) + "/image.png";

            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deal;
    }

    public static User parseLoginResponse(String response, Context context) {
        try {
            JSONObject object = new JSONObject(response);
            Boolean success = Boolean.parseBoolean(object.optString("success"));
            String token, name, email, mobile, mixpanelId;
            if (success) {
                token = object.optString("token");
                JSONObject userDetails = object.getJSONObject("details");
                name = userDetails.optString("name");
                email = userDetails.optString("email");
                mobile = userDetails.optString("mobile");
                mixpanelId = userDetails.optString("mixpanelId");

                return new User(name, email, mobile, token, mixpanelId);
            } else if (object.optString("error").equalsIgnoreCase("INVALID_CREDENTIALS")) {
                Toast.makeText(context, "Wrong username or password", Toast.LENGTH_LONG).show();
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String[] parseSignupResponse(String response, final Activity activity) {
        try {
            JSONObject object = new JSONObject(response);
            Boolean success = Boolean.parseBoolean(object.optString("success"));
            String[] token = {};
            if (success) {
                token[0] = object.optString("token");
                token[1] = object.optString("mixpanelid");
                return token;
            } else if (object.optString("error").equalsIgnoreCase("USER_EXIST")) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("User Exists")
                                .setMessage("An account already exists with this email id. Please Log In to continue")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        activity.startActivity(new Intent(activity, LogIn.class));
                                        activity.finish();
                                    }
                                })
                                .show();

                    }
                });
                return new String[]{};
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
