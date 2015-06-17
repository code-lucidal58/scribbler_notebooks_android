package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.scribblernotebooks.scribblernotebooks.Handlers.UserHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.CodeSigner;
import java.util.HashMap;

/**
 * Created by Jibin_ism on 19-May-15.
 */
public class Deal implements Parcelable {

    private String id, title, category, shortDescription, imageUrl, longDescription;
    private Boolean isFav=false,isFeatured=false;
    private String couponCode;

    public Deal(){
        super();
    }

    public Deal(String id, String title, String category, String shortDescription, String imageUrl, Boolean isFav) {
        this(id,title,category,shortDescription,imageUrl,"",isFav,false);
    }

    public Deal (String id, String title, String category, String shortDescription, String imageUrl,
                 String longDescription, Boolean isFav,Boolean isFeatured)
    {
        this.id = id;
        this.title = title;
        this.category = category;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.longDescription = longDescription;
        this.isFav = isFav;
        this.isFeatured=isFeatured;
    }

    private Deal(Parcel in){
        id=in.readString();
        title=in.readString();
        category=in.readString();
        shortDescription=in.readString();
        imageUrl=in.readString();
        longDescription=in.readString();
        isFav=Boolean.parseBoolean(in.readString());
        isFeatured=Boolean.parseBoolean(in.readString());
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
    }

    public static final Parcelable.Creator<Deal> CREATOR=new Parcelable.Creator<Deal>(){
        @Override
        public Deal createFromParcel(Parcel source) {
            return new Deal(source);
        }

        @Override
        public Deal[] newArray(int size) {
            return new Deal[size];
        }
    };



    /**
     * Sending statistics to the server about like and share
     */
    public void sendLikeStatus(final Context context,Boolean isFav){
        //Code to synchronise with server
        User user= Constants.getUser(context);
        String email=user.getEmail();
        String token=user.getToken();
        String id=this.getId();
        String liked=String.valueOf(isFav);

        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data=new HashMap<String, String>();
                data.put(Constants.POST_EMAIL,params[0]);
                data.put(Constants.POST_TOKEN,params[1]);
                data.put("dealId",params[2]);
                data.put("liked",params[3]);

                try{
                    URL url=new URL(Constants.ServerUrls.likeDeal);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");

                    OutputStream os=connection.getOutputStream();
                    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    writer.write(Constants.getPostDataString(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response=reader.readLine();
                    JSONObject object=new JSONObject(response);
                    if(Boolean.parseBoolean(object.optString("success"))){
                        UserHandler handler=new UserHandler(context);
                        if(Boolean.parseBoolean(params[3]))
                            handler.addDeal(params[2]);
                        else
                        handler.removeDeal(params[2]);
                        Log.e("Deal Favorite", "Deal Favorited");
                        handler.close();
                    }



                }
                catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(email,token,id,liked);
    }

    public void sendShareStatus(){
        //Code to synchronise with server
        
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
        sendLikeStatus(context,isFav);
        //Mixpanel code
        MixpanelAPI mixpanelAPI=Constants.getMixPanelInstance(context);
        JSONObject props=new JSONObject();
        try {
            props.put("Like",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanelAPI.track("User", props);
    }
    public void setIsFav(Boolean isFav) {
        this.isFav = isFav;
    }

    public void setIsFeatured(Boolean isFeatured){
        this.isFeatured=isFeatured;
    }

    public void setCouponCode(String couponCode){
        this.couponCode=couponCode;
    }

    public void setLongDescription(String longDescription){ this.longDescription=longDescription; }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){ return this.id; }

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

    public String getLongDescription(){return this.longDescription;}

    public Boolean isFeatured()
    {
        return this.isFeatured;
    }

    public String getCouponCode(){
        return this.couponCode;
    }

}
