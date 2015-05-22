package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Aanisha on 11-May-15.
 */

public class ParseJson {

    public static ArrayList<Deal> getParsedData(String url){

        ArrayList<Deal> dealsList=new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(url);
            JSONArray jsonArray = jsonObject.optJSONArray("listview_item_deals");
            int lengthJsonArr= jsonArray.length();

            for(int i=0;i<lengthJsonArr;i++)
            {
                JSONObject jsonChildNode = jsonArray.getJSONObject(i);

                /****Fetch node values****/
                String id = jsonChildNode.optString(Constants.TAG_ID);
                String category = jsonChildNode.optString(Constants.TAG_CATEGORY);
                String dealName = jsonChildNode.optString(Constants.TAG_DEAL_NAME);
                String image = jsonChildNode.optString(Constants.TAG_IMAGE_URL);
                String shortDesc = jsonChildNode.optString(Constants.TAG_SHORT_DESCRIPTION);
                String longDesc = jsonChildNode.optString(Constants.TAG_LONG_DESCRIPTION);
                Deal deal=new Deal(id,dealName,category,shortDesc,image,false,longDesc);

                dealsList.add(deal);
            }

        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        return dealsList;
    }
}
