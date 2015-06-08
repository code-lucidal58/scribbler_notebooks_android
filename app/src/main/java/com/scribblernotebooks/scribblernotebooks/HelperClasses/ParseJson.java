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
//        Log.e("JSON","jsonResponse "+jsonResponse);
        try {
            JSONObject jsonChild=new JSONObject(jsonResponse);
            String success=jsonChild.optString("success");

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deal;
    }

}
