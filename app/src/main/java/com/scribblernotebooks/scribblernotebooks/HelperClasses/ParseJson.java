package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aanisha on 11-May-15.
 */
public class ParseJson {
    public static ArrayList<HashMap<String,String>> getParsedData(String url){

        ArrayList<HashMap<String,String>> dealsList=new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(url);
            JSONArray jsonArray = jsonObject.optJSONArray("deals");
            int lengthJsonArr= jsonArray.length();

            for(int i=0;i<lengthJsonArr;i++)
            {
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                String id = jsonChildNode.optString(Constants.TAG_ID);
                String category = jsonChildNode.optString(Constants.TAG_CATEGORY);
                String newhot = jsonChildNode.optString(Constants.TAG_NEW);
                String dealName = jsonChildNode.optString(Constants.TAG_DEAL_NAME);
                String image = jsonChildNode.optString(Constants.TAG_IMAGE_URL);
                String shortDesc = jsonChildNode.optString(Constants.TAG_SHORT_DESCRIPTION);
                String longDesc = jsonChildNode.optString(Constants.TAG_LONG_DESCRIPTION);

                HashMap<String, String> deals = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                deals.put(Constants.TAG_ID, id);
                deals.put(Constants.TAG_CATEGORY, category.replace(" ", "\n"));
                deals.put(Constants.TAG_DEAL_NAME, dealName);
                deals.put(Constants.TAG_IMAGE_URL, image);
                deals.put(Constants.TAG_SHORT_DESCRIPTION, shortDesc);
                deals.put(Constants.TAG_NEW, newhot);

                dealsList.add(deals);
            }

        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return dealsList;
    }
}
