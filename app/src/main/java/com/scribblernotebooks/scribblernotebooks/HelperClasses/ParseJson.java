package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Aanisha on 11-May-15.
 */

public class ParseJson {

    public static ArrayList<Deal> getParsedData(String url,Context context, Boolean b){
        ArrayList<Deal> dealsList=new ArrayList<>();
        try{
            JSONObject jsonChild = new JSONObject(url);
            String success=jsonChild.optString("success");
            if(success.equalsIgnoreCase("false")){
                return null;
            }
            JSONArray jsonArray=jsonChild.optJSONArray("message");

            int lengthJsonArr= jsonArray.length();

            for(int i=0;i<lengthJsonArr;i++)
            {
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                Deal deal=parseSingleDeal(jsonChildNode.toString());
                dealsList.add(deal);

            }

        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return dealsList;
    }


    public static Deal parseSingleDeal(String jsonResponse){
        Deal deal=new Deal();
        Log.e("JSON","jsonResponse "+jsonResponse);
        try {
            JSONObject jsonChild=new JSONObject(jsonResponse);

            String id,title,category,sdesp,ldesp,imgurl;

            id=jsonChild.optString(Constants.TAG_ID);
            title=jsonChild.optString(Constants.TAG_DEAL_NAME);
            category=jsonChild.optString(Constants.TAG_CATEGORY);
            sdesp=jsonChild.optString(Constants.TAG_SHORT_DESCRIPTION);
            ldesp=jsonChild.optString(Constants.TAG_LONG_DESCRIPTION);
            // Image url http://www.ucarecdn.com/<image UUID>/image.png
            imgurl="http://www.ucarecdn.com/"+jsonChild.optString(Constants.TAG_IMAGE_UUID)+"/image.png";

            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);
            Log.e("Done","Done");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Deal Object",deal.getId()+deal.getTitle()+deal.getCategory()+deal.getLongDescription()+deal.getShortDescription());
        return deal;
    }
    public static Deal parseSingleDealDetail(String jsonResponse){
        Deal deal=new Deal();
        Log.e("JSON","jsonResponse "+jsonResponse);
        try {
            JSONObject jsonObject=new JSONObject(jsonResponse);
            JSONObject jsonChild=jsonObject.optJSONObject("message");
            String success=jsonObject.optString("success");

            String id,title,category,sdesp,ldesp,imgurl;

            id=jsonChild.optString(Constants.TAG_ID);
            title=jsonChild.optString(Constants.TAG_DEAL_NAME);
            category=jsonChild.optString(Constants.TAG_CATEGORY);
            sdesp=jsonChild.optString(Constants.TAG_SHORT_DESCRIPTION);
            ldesp=jsonChild.optString(Constants.TAG_LONG_DESCRIPTION);
            // Image url http://www.ucarecdn.com/<image UUID>/image.png
            imgurl="http://www.ucarecdn.com/"+jsonChild.optString(Constants.TAG_IMAGE_UUID)+"/image.png";

            deal.setId(id);
            deal.setTitle(title);
            deal.setCategory(category);
            deal.setImageUrl(imgurl);
            deal.setShortDescription(sdesp);
            deal.setLongDescription(ldesp);
            Log.e("Done","Done");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Deal Object",deal.getId()+deal.getTitle()+deal.getCategory()+deal.getLongDescription()+deal.getShortDescription());
        return deal;
    }

    public static User parseLoginResponse(String response){
        try {
            JSONObject object=new JSONObject(response);
            Boolean success=Boolean.parseBoolean(object.optString("success"));
            String token,name,email,mobile,mixpanelId;
            if(success){
                token=object.optString("token");
                mixpanelId=object.optString("mixpanelId");
                JSONObject userDetails=object.getJSONObject("details");
                name=userDetails.optString("name");
                email=userDetails.optString("email");
                mobile=userDetails.optString("mobile");

                return new User(name,email,mobile,token,mixpanelId);
            }
            else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
