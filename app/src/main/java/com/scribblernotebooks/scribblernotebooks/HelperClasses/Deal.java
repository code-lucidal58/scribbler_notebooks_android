package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.scribblernotebooks.scribblernotebooks.Handlers.DatabaseHandler;
import com.scribblernotebooks.scribblernotebooks.Handlers.UserHandler;

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
import java.util.HashMap;

/**
 * Created by Jibin_ism on 19-May-15.
 */
public class Deal implements Parcelable {

    private static final String TAG="Deal";
    public enum Range {
        VERY_LOW, LOW, MEDIAN, HIGH, VERY_HIGH
    }


    private String id = "", title = "", category = "", shortDescription = "", imageUrl = "", longDescription = "";
    private Boolean isFav = false, isFeatured = false;
    private String couponCode = "";

    String companyName = "", location = "", dealType = "", tnc = "";
    boolean isGroup = false;
    String validity = "";

    public Deal() {
        super();
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl, Boolean isFav) {
        this(id, title, category, shortDescription, imageUrl, "", isFav, false);
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl,
                String longDescription, Boolean isFav, Boolean isFeatured) {
        this(id, title, category, shortDescription, imageUrl, longDescription, isFav, isFeatured, "");
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl,
                String longDescription, Boolean isFav, Boolean isFeatured, String couponCode) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.longDescription = longDescription;
        this.isFav = isFav;
        this.isFeatured = isFeatured;
        this.couponCode = couponCode;
    }

    private Deal(Parcel in) {
        id = in.readString();
        title = in.readString();
        category = in.readString();
        shortDescription = in.readString();
        imageUrl = in.readString();
        longDescription = in.readString();
        isFav = Boolean.parseBoolean(in.readString());
        isFeatured = Boolean.parseBoolean(in.readString());
        couponCode = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(shortDescription);
        dest.writeString(imageUrl);
        dest.writeString(longDescription);
        dest.writeString(String.valueOf(isFav));
        dest.writeString(String.valueOf(isFeatured));
        dest.writeString(couponCode);
    }

    public static final Parcelable.Creator<Deal> CREATOR = new Parcelable.Creator<Deal>() {
        @Override
        public Deal createFromParcel(Parcel source) {
            return new Deal(source);
        }

        @Override
        public Deal[] newArray(int size) {
            return new Deal[size];
        }
    };


    int lowerLimit = 0;
    int upperLimit = Integer.MAX_VALUE;

    public Range getPriceRange() {
        if (upperLimit < 250) {
            return Range.VERY_HIGH;
        } else if (upperLimit < 350) {
            return Range.LOW;
        } else if (upperLimit < 500) {
            return Range.MEDIAN;
        } else if (upperLimit < 750) {
            return Range.HIGH;
        } else {
            return Range.VERY_HIGH;
        }
    }

    /**
     * Sending statistics to the server about like and share
     */
    public void sendLikeStatus(final Context context, final Boolean isFav) {
        //Code to synchronise with server
        User user = Constants.getUser(context);
        String email = user.getEmail();
        String token = user.getToken();
        String id = this.getId();
        String liked = String.valueOf(isFav);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String email = params[0];
                String token = params[1];
                String id = params[2];
                String liked = params[3];

                data.put("token", token);
                data.put("email", email);
                data.put("id", id);

                URL url;
                try {
                    url = new URL(Constants.getLikeDealUrl(id));
                    Log.e("Deal", "Liking deal url:" + url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setDoInput(true);
                    if (isFav) {
                        connection.setRequestMethod("POST");
                        Log.e("Deal", "POST method");
                        connection.setDoOutput(true);

                        Log.e("Deal", "writing data");
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(Constants.getPostDataString(data));
                        Log.e("Deal", "Post Data: " + Constants.getPostDataString(data));
                        writer.flush();
                        writer.close();
                        os.close();
                    } else {
                        connection.setRequestMethod("DELETE");
                        Log.e("Deal", "DELETE method");
                    }


                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String s = reader.readLine();
                    JSONObject jsonObject = new JSONObject(s);
                    boolean success = Boolean.parseBoolean(jsonObject.optString("success"));
                    if (success) {
                        UserHandler handler1 = new UserHandler(context);
                        handler1.addDeal(id);
                        handler1.close();
                    }

                } catch (Exception e) {
                    Log.e("Deal", "Like Deal Exception");
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("Deal", "Liked Deal");
            }
        }.execute(email, token, id, liked);
        //Mixpanel code
        MixpanelAPI mixpanelAPI = Constants.getMixPanelInstance(context);
        JSONObject props = new JSONObject();
        try {
            props.put("Like", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanelAPI.track("User", props);
    }

    public void sendShareStatus(final Context context) {
        //Code to synchronise with server

        User user = Constants.getUser(context);
        String email = user.getEmail();
        String token = user.getToken();
        String id = this.getId();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put(Constants.POST_EMAIL, params[0]);
                data.put(Constants.POST_TOKEN, params[1]);
                data.put("dealId", params[2]);

                try {
                    URL url = new URL(Constants.ServerUrls.shareDeal);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();
                    UserHandler handler = new UserHandler(context);
                    handler.addSharedDeal(params[2]);
                    Log.e("Deal Shared", "Deal Shared");
                    handler.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(email, token, id);

        //Mixpanel code
        MixpanelAPI mixpanelAPI = Constants.getMixPanelInstance(context);
        JSONObject props = new JSONObject();
        try {
            props.put("Share", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanelAPI.track("User", props);
    }

    /**
     * Getter and Setter functions
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIsFav(Context context, Boolean isFav) {
        this.isFav = isFav;
        sendLikeStatus(context, isFav);
    }

    public void setIsFav(Boolean isFav) {
        this.isFav = isFav;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCategory() {
        return this.category;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Boolean isFavorited() {
        return this.isFav;
    }

    public String getLongDescription() {
        return this.longDescription;
    }

    public Boolean isFeatured() {
        return this.isFeatured;
    }

    public String getCouponCode() {
        return this.couponCode;
    }

    public Boolean getIsFav() {
        return isFav;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void claimDeal(final Context context) {
        final Deal deal = this;
        Log.e("Deal", "Claiming deal:" + id);
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String id = params[0];
                User user = Constants.getUser(context);
                try {
                    Log.e("Deal", "Claimed Deal:" + id);
                    URL url = new URL(Constants.ServerUrls.claimDeal);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setRequestProperty("Authorization", "Bearer " + user.getToken());
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("dealId", id);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String s = reader.readLine();
                    JSONObject jsonObject = new JSONObject(s);
                    String success=jsonObject.getString("success");
                    Log.e(TAG,"Response: "+success+" "+s);
                    if (success.equalsIgnoreCase("true")) {
                        dealListener.onDealClaimed(deal.getCouponCode());
                        DatabaseHandler handler = new DatabaseHandler(context);
                        handler.addUnusedDeal(deal);
                        handler.addClaimedDeal(deal);
                        Log.e(TAG,"CouponCode: "+deal.getCouponCode());
                        handler.close();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dealListener.onDealClaimed("");
                return null;
            }

        }.execute(id);
    }

    public void useDeal(final Context context, String confirmCode){
        final Deal deal = this;
        Log.e("Deal", "Claiming deal:" + id);
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String id = params[0];
                User user = Constants.getUser(context);
                try {
                    Log.e("Deal", "Used Deal:" + id);
                    URL url = new URL(Constants.ServerUrls.useDeal);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setRequestProperty("Authorization", "Bearer " + user.getToken());
                    //TODO: Update this function
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("dealId", id);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String s = reader.readLine();
                    JSONObject jsonObject = new JSONObject(s);
                    String success=jsonObject.getString("success");
                    Log.e(TAG,"Response: "+success+" "+s);
                    if (success.equalsIgnoreCase("true")) {
                        dealUseListener.onDealUsed(true);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dealUseListener.onDealUsed(false);
                return null;
            }

        }.execute(id);
    }




    DealListener dealListener;
    public interface DealListener {
        void onDealClaimed(String couponCode);
    }
    public void setDealListener(DealListener dealListener) {
        this.dealListener = dealListener;
    }

    DealUseListener dealUseListener;
    public interface DealUseListener{
        void onDealUsed(Boolean success);
    }
    public void setDealUseListener(DealUseListener dealUseListener){
        this.dealUseListener=dealUseListener;
    }

}
